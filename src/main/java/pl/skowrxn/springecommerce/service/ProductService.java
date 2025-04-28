package pl.skowrxn.springecommerce.service;

import org.springframework.web.multipart.MultipartFile;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.response.ProductListResponse;

import java.io.IOException;

public interface ProductService {

    ProductDTO createProduct(Long categoryId, ProductDTO product);

    ProductListResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

    ProductListResponse getProductsByCategoryId(Long id, int pageNumber, int pageSize, String sortBy, String sortDir);

    ProductListResponse getProductsByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProductById(Long id);

    ProductDTO updateProductImage(Long id, MultipartFile image) throws IOException;
}
