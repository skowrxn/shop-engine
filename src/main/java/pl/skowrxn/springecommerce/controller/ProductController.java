package pl.skowrxn.springecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.response.ProductListResponse;
import pl.skowrxn.springecommerce.service.ProductService;

import java.io.IOException;
import java.net.URI;

@RestController
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        ProductDTO createdProduct = this.productService.createProduct(categoryId, productDTO);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())).body(createdProduct);
    }

    @GetMapping("/products")
    public ResponseEntity<ProductListResponse> getAllProducts(@RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                              @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
                                                              @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                              @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        ProductListResponse productListResponse = this.productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(productListResponse);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ProductListResponse> getProductsByCategoryId(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        ProductListResponse productListResponse = this.productService.getProductsByCategoryId(id, pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(productListResponse);
    }

    @GetMapping("/products/{keyword}")
    public ResponseEntity<ProductListResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                    @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                                    @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
                                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                    @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(this.productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortDir));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody @Valid ProductDTO productDTO, @PathVariable Long id) {
        ProductDTO updatedProduct = this.productService.updateProduct(id, productDTO);
        return ResponseEntity.accepted().body(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        this.productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/products/{id}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.accepted().body(this.productService.updateProductImage(id, image));
    }

}
