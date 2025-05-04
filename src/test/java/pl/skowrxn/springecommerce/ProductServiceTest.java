package pl.skowrxn.springecommerce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.response.ProductListResponse;
import pl.skowrxn.springecommerce.entity.Category;
import pl.skowrxn.springecommerce.entity.Product;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.CategoryRepository;
import pl.skowrxn.springecommerce.repository.ProductRepository;
import pl.skowrxn.springecommerce.service.FileService;
import pl.skowrxn.springecommerce.service.ProductServiceImpl;
import pl.skowrxn.springecommerce.service.UserService;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileService fileService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testCreateProduct_Success() {
        Long categoryId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(100.0);
        productDTO.setDiscount(10.0);
        productDTO.setDescription("Test Description");
        productDTO.setStockQuantity(50);

        User user = new User();
        user.setId(1L);
        user.setProducts(new ArrayList<>());

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setDiscount(10.0);
        product.setDescription("Test Description");
        product.setStockQuantity(50);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");
        savedProduct.setPrice(90.0);
        savedProduct.setDiscount(10.0);
        savedProduct.setDescription("Test Description");
        savedProduct.setStockQuantity(50);
        savedProduct.setCategory(category);
        savedProduct.setUser(user);

        ProductDTO savedProductDTO = new ProductDTO();
        savedProductDTO.setId(1L);
        savedProductDTO.setName("Test Product");
        savedProductDTO.setPrice(90.0);
        savedProductDTO.setDiscount(10.0);
        savedProductDTO.setDescription("Test Description");
        savedProductDTO.setStockQuantity(50);

        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(modelMapper.map(savedProduct, ProductDTO.class)).thenReturn(savedProductDTO);

        ProductDTO result = productService.createProduct(categoryId, productDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(90.0, result.getPrice());
        assertEquals(10.0, result.getDiscount());
        assertEquals("Test Description", result.getDescription());
        assertEquals(50, result.getStockQuantity());

        verify(authUtil).getLoggedInUser();
        verify(categoryRepository).findById(categoryId);
        verify(modelMapper).map(productDTO, Product.class);
        verify(productRepository).save(product);
        verify(modelMapper).map(savedProduct, ProductDTO.class);
    }

    @Test
    void testCreateProduct_CategoryNotFound() {
        Long categoryId = 999L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(categoryId, productDTO));

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(authUtil, modelMapper, productRepository);
    }

    @Test
    void testGetAllProducts() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDir = "asc";

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(90.0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(80.0);

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(pageNumber, pageSize), products.size());

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Product 1");
        productDTO1.setPrice(90.0);

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Product 2");
        productDTO2.setPrice(80.0);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(product1, ProductDTO.class)).thenReturn(productDTO1);
        when(modelMapper.map(product2, ProductDTO.class)).thenReturn(productDTO2);

        ProductListResponse result = productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir);

        assertNotNull(result);
        assertEquals(pageNumber, result.getPage());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isLastPage());
        assertEquals(2, result.getProducts().size());

        verify(productRepository).findAll(any(Pageable.class));
        verify(modelMapper).map(product1, ProductDTO.class);
        verify(modelMapper).map(product2, ProductDTO.class);
    }

    @Test
    void testGetProductsByCategoryId_Success() {
        Long categoryId = 1L;
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDir = "asc";

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setCategory(category);

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(pageNumber, pageSize), products.size());

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Product 1");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Product 2");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategory(eq(category), any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(product1, ProductDTO.class)).thenReturn(productDTO1);
        when(modelMapper.map(product2, ProductDTO.class)).thenReturn(productDTO2);

        ProductListResponse result = productService.getProductsByCategoryId(categoryId, pageNumber, pageSize, sortBy, sortDir);

        assertNotNull(result);
        assertEquals(pageNumber, result.getPage());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isLastPage());
        assertEquals(2, result.getProducts().size());

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).findByCategory(eq(category), any(Pageable.class));
        verify(modelMapper).map(product1, ProductDTO.class);
        verify(modelMapper).map(product2, ProductDTO.class);
    }

    @Test
    void testGetProductsByCategoryId_CategoryNotFound() {
        Long categoryId = 999L;
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDir = "asc";

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            productService.getProductsByCategoryId(categoryId, pageNumber, pageSize, sortBy, sortDir));

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository, modelMapper);
    }

    @Test
    void testGetProductsByKeyword() {
        String keyword = "test";
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDir = "asc";

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(pageNumber, pageSize), products.size());

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Test Product 1");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Test Product 2");

        when(productRepository.findByNameLikeIgnoreCase(eq("%" + keyword + "%"), any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(product1, ProductDTO.class)).thenReturn(productDTO1);
        when(modelMapper.map(product2, ProductDTO.class)).thenReturn(productDTO2);

        ProductListResponse result = productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortDir);

        assertNotNull(result);
        assertEquals(pageNumber, result.getPage());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isLastPage());
        assertEquals(2, result.getProducts().size());

        verify(productRepository).findByNameLikeIgnoreCase(eq("%" + keyword + "%"), any(Pageable.class));
        verify(modelMapper).map(product1, ProductDTO.class);
        verify(modelMapper).map(product2, ProductDTO.class);
    }

    @Test
    void testUpdateProduct_Success() {
        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setPrice(120.0);
        productDTO.setDiscount(20.0);
        productDTO.setDescription("Updated Description");
        productDTO.setStockQuantity(30);
        productDTO.setImage("updated-image.jpg");

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Original Product");
        existingProduct.setPrice(100.0);
        existingProduct.setDiscount(10.0);
        existingProduct.setDescription("Original Description");
        existingProduct.setStockQuantity(50);
        existingProduct.setImage("original-image.jpg");

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(96.0);
        updatedProduct.setDiscount(20.0);
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setStockQuantity(30);
        updatedProduct.setImage("updated-image.jpg");

        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(productId);
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setPrice(96.0);
        updatedProductDTO.setDiscount(20.0);
        updatedProductDTO.setDescription("Updated Description");
        updatedProductDTO.setStockQuantity(30);
        updatedProductDTO.setImage("updated-image.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(modelMapper.map(updatedProduct, ProductDTO.class)).thenReturn(updatedProductDTO);

        ProductDTO result = productService.updateProduct(productId, productDTO);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Updated Product", result.getName());
        assertEquals(96.0, result.getPrice());
        assertEquals(20.0, result.getDiscount());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(30, result.getStockQuantity());
        assertEquals("updated-image.jpg", result.getImage());

        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
        verify(modelMapper).map(updatedProduct, ProductDTO.class);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        Long productId = 999L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, productDTO));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testDeleteProductById_Success() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(productId);

        productService.deleteProductById(productId);

        verify(productRepository).findById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void testDeleteProductById_ProductNotFound() {
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(productId));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateProductImage_Success() throws IOException {
        Long productId = 1L;
        MockMultipartFile image = new MockMultipartFile(
                "file", 
                "test-image.jpg",
                "image/jpeg", 
                "test image content".getBytes());

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Test Product");
        existingProduct.setImage("old-image.jpg");

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Test Product");
        updatedProduct.setImage("new-image.jpg");

        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(productId);
        updatedProductDTO.setName("Test Product");
        updatedProductDTO.setImage("new-image.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(fileService.uploadImage(image)).thenReturn("new-image.jpg");
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(modelMapper.map(updatedProduct, ProductDTO.class)).thenReturn(updatedProductDTO);

        ProductDTO result = productService.updateProductImage(productId, image);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals("new-image.jpg", result.getImage());

        verify(productRepository).findById(productId);
        verify(fileService).uploadImage(image);
        verify(productRepository).save(existingProduct);
        verify(modelMapper).map(updatedProduct, ProductDTO.class);
    }

    @Test
    void testUpdateProductImage_ProductNotFound() throws IOException {
        Long productId = 999L;
        MockMultipartFile image = new MockMultipartFile(
                "file", 
                "test-image.jpg",
                "image/jpeg", 
                "test image content".getBytes());

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductImage(productId, image));

        verify(productRepository).findById(productId);
        verifyNoInteractions(fileService, modelMapper);
    }
}