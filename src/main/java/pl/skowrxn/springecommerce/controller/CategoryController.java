package pl.skowrxn.springecommerce.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skowrxn.springecommerce.dto.CategoryDTO;
import pl.skowrxn.springecommerce.dto.response.CategoryListResponse;
import pl.skowrxn.springecommerce.service.CategoryService;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private CategoryService categoryService;
    private ModelMapper modelMapper;

    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = this.categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
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
