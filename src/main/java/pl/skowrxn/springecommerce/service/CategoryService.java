package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.CategoryDTO;
import pl.skowrxn.springecommerce.dto.CategoryListResponse;

public interface CategoryService {

    CategoryDTO getCategoryById(Long id);

    CategoryListResponse getAllCategories(Integer page, Integer pageSize, String sortBy, String sortOrder);

    void deleteCategoryById(Long id);

    CategoryDTO createCategory(CategoryDTO category);

    CategoryDTO updateCategory(Long id, CategoryDTO category);

}
