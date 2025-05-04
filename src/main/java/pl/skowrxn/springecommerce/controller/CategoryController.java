package pl.skowrxn.springecommerce.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skowrxn.springecommerce.dto.CategoryDTO;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.response.CategoryListResponse;
import pl.skowrxn.springecommerce.dto.response.ProductListResponse;
import pl.skowrxn.springecommerce.service.CategoryService;
import pl.skowrxn.springecommerce.service.ProductService;

import java.net.URI;

@RestController
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = this.categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/categories/{id}/products")
    public ResponseEntity<ProductListResponse> getProductsByCategoryId(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        ProductListResponse productListResponse = this.productService.getProductsByCategoryId(id, pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(productListResponse);
    }

    @PostMapping("/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        ProductDTO createdProduct = this.productService.createProduct(categoryId, productDTO);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())).body(createdProduct);
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoryListResponse> getCategories(@RequestParam(name="page", defaultValue = "0") Integer page,
                                                              @RequestParam(name="pageSize", defaultValue = "20") Integer pageSize,
                                                              @RequestParam(name="sortBy", defaultValue = "id") String sortBy,
                                                              @RequestParam(name="sortOrder", defaultValue = "asc") String sortOrder) {
        CategoryListResponse categoryListResponse = this.categoryService.getAllCategories(page, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(categoryListResponse);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = this.categoryService.createCategory(categoryDTO);
        URI location = URI.create("/api/categories/" + createdCategory.getId());
        return ResponseEntity.created(location).body(createdCategory);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = this.categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.accepted().body(updatedCategory);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        this.categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();

    }

}
