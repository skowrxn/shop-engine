package pl.skowrxn.springecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.response.ProductListResponse;
import pl.skowrxn.springecommerce.entity.Category;
import pl.skowrxn.springecommerce.entity.Product;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.CategoryRepository;
import pl.skowrxn.springecommerce.repository.ProductRepository;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, UserService userService,
                              CategoryRepository categoryRepository, FileService fileService,
                              AuthUtil authUtil, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO) {
        User seller = this.authUtil.getLoggedInUser();
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Product product = this.modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setUser(seller);
        double price = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
        product.setPrice(price);

        Product createdProduct = this.productRepository.save(product);
        seller.getProducts().add(product);

        return this.modelMapper.map(createdProduct, ProductDTO.class);
    }

    @Override
    public ProductListResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = Arrays.asList("desc", "descending").contains(sortDir.toLowerCase())
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> products = this.productRepository.findAll(pageable);
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> this.modelMapper.map(product, ProductDTO.class))
                .toList();

        return new ProductListResponse(productDTOs, pageNumber, pageSize, products.getTotalPages(), products.getTotalElements(), products.isLast());

    }

    @Override
    public ProductListResponse getProductsByCategoryId(Long id, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = this.translateSort(sortDir, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        Page<Product> products = this.productRepository.findByCategory(category, pageable);
        List<ProductDTO> productDTOS = products.stream().map(product -> this.modelMapper.map(product, ProductDTO.class)).toList();
        return new ProductListResponse(productDTOS, pageNumber, pageSize, products.getTotalPages(), products.getTotalElements(), products.isLast());
    }

    @Override
    public ProductListResponse getProductsByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = this.translateSort(sortDir, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> products = this.productRepository.findByNameLikeIgnoreCase('%' + keyword + '%', pageable);
        List<ProductDTO> productDTOS = products.stream().map(product -> this.modelMapper.map(product, ProductDTO.class)).toList();
        return new ProductListResponse(productDTOS, pageNumber, pageSize, products.getTotalPages(), products.getTotalElements(), products.isLast());
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        existingProduct.setName(productDTO.getName());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDiscount(productDTO.getDiscount());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setImage(productDTO.getImage());
        existingProduct.setPrice(productDTO.getPrice() - (productDTO.getPrice() * productDTO.getDiscount() / 100));

        Product savedProduct = this.productRepository.save(existingProduct);
        return this.modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        this.productRepository.deleteById(id);
    }


    @Override
    public ProductDTO updateProductImage(Long id, MultipartFile image) throws IOException {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        String fileName = this.fileService.uploadImage(image);
        product.setImage(fileName);
        Product savedProduct = this.productRepository.save(product);
        return this.modelMapper.map(savedProduct, ProductDTO.class);
    }

    private Sort translateSort(String sortDir, String sortBy) {
        return Arrays.asList("desc", "descending").contains(sortDir.toLowerCase())
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    }
}
