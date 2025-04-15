package pl.skowrxn.springecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.skowrxn.springecommerce.dto.CategoryDTO;
import pl.skowrxn.springecommerce.dto.CategoryListResponse;
import pl.skowrxn.springecommerce.entity.Category;
import pl.skowrxn.springecommerce.exception.ResourceConflictException;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return this.modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryListResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = Arrays.asList("desc", "descending").contains(sortOrder.toLowerCase())
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPage = this.categoryRepository.findAll(pageDetails);
        List<CategoryDTO> categoryDTOs = categoryPage.stream()
                .map(category -> this.modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryListResponse categoryListResponse = new CategoryListResponse();
        categoryListResponse.setCategories(categoryDTOs);
        categoryListResponse.setPage(pageNumber);
        categoryListResponse.setPageSize(pageSize);
        categoryListResponse.setTotalPages(categoryPage.getTotalPages());
        categoryListResponse.setTotalElements(categoryPage.getTotalElements());
        categoryListResponse.setLastPage(categoryPage.isLast());

        return categoryListResponse;
    }

    @Override
    public void deleteCategoryById(Long id) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        this.categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = this.modelMapper.map(categoryDTO, Category.class);
        String categoryName = categoryDTO.getName();
        if (this.categoryRepository.findAll().stream()
                .anyMatch(category1 -> category1.getName().equalsIgnoreCase(categoryName))) {
            throw new ResourceConflictException("Category", "name", categoryName);
        }
        Category savedCategory = this.categoryRepository.save(category);
        return this.modelMapper.map(savedCategory, CategoryDTO.class);

    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        String newCategoryName = categoryDTO.getName();
        if (this.categoryRepository.findAll().stream()
                .anyMatch(category1 -> category1.getName().equalsIgnoreCase(newCategoryName) && !category1.getId().equals(id))) {
            throw new ResourceConflictException("Category", "name", newCategoryName);
        }
        existingCategory.setName(categoryDTO.getName());
        Category savedCategory = this.categoryRepository.save(existingCategory);
        return this.modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
