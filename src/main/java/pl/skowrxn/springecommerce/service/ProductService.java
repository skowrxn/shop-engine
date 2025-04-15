package pl.skowrxn.springecommerce.service;

import org.springframework.web.multipart.MultipartFile;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.ProductListResponse;
import pl.skowrxn.springecommerce.entity.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    public ProductDTO createProduct(Long categoryId, ProductDTO product);

    public ProductListResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

    ProductListResponse getProductsByCategoryId(Long id, int pageNumber, int pageSize, String sortBy, String sortDir);

    ProductListResponse getProductsByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProductById(Long id);

    ProductDTO updateProductImage(Long id, MultipartFile image) throws IOException;
}
