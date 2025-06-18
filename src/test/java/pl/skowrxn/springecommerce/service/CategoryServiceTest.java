package pl.skowrxn.springecommerce.service;

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
import pl.skowrxn.springecommerce.dto.CategoryDTO;
import pl.skowrxn.springecommerce.dto.response.CategoryListResponse;
import pl.skowrxn.springecommerce.entity.Category;
import pl.skowrxn.springecommerce.exception.ResourceConflictException;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testGetCategoryById_CategoryExists() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setName("Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Electronics", result.getName());

        verify(categoryRepository).findById(categoryId);
        verify(modelMapper).map(category, CategoryDTO.class);
    }

    @Test
    void testGetCategoryById_CategoryNotFound() {
        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetAllCategories() {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "name";
        String sortOrder = "asc";

        Category category1 = new Category();
        Category category2 = new Category();

        category1.setId(1L);
        category1.setName("Electronics");
        
        category2.setId(2L);
        category2.setName("Clothing");

        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(pageNumber, pageSize), categories.size());

        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId(1L);
        categoryDTO1.setName("Electronics");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setId(2L);
        categoryDTO2.setName("Clothing");

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(modelMapper.map(category1, CategoryDTO.class)).thenReturn(categoryDTO1);
        when(modelMapper.map(category2, CategoryDTO.class)).thenReturn(categoryDTO2);

        CategoryListResponse result = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);

        assertNotNull(result);
        assertEquals(pageNumber, result.getPage());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isLastPage());
        assertEquals(2, result.getCategories().size());

        verify(categoryRepository).findAll(any(Pageable.class));
        verify(modelMapper).map(category1, CategoryDTO.class);
        verify(modelMapper).map(category2, CategoryDTO.class);
    }

    @Test
    void testDeleteCategoryById_CategoryExists() {
        Long categoryId = 1L;
        Category category = new Category();

        category.setId(categoryId);
        category.setName("Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void testDeleteCategoryById_CategoryNotFound() {
        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(categoryId));

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testCreateCategory_Success() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");

        Category category = new Category();
        category.setName("Electronics");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");

        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setId(1L);
        savedCategoryDTO.setName("Electronics");

        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDTO.class)).thenReturn(savedCategoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());

        verify(modelMapper).map(categoryDTO, Category.class);
        verify(categoryRepository).findAll();
        verify(categoryRepository).save(category);
        verify(modelMapper).map(savedCategory, CategoryDTO.class);
    }

    @Test
    void testCreateCategory_NameConflict() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");

        Category existingCategory = new Category();

        existingCategory.setId(1L);
        existingCategory.setName("electronics");

        Category category = new Category();
        category.setName("Electronics");

        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(categoryRepository.findAll()).thenReturn(List.of(existingCategory));

        assertThrows(ResourceConflictException.class, () -> categoryService.createCategory(categoryDTO));

        verify(modelMapper).map(categoryDTO, Category.class);
        verify(categoryRepository).findAll();
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_Success() {
        Long categoryId = 1L;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Electronics");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Electronics");

        Category savedCategory = new Category();
        savedCategory.setId(categoryId);
        savedCategory.setName("Updated Electronics");
        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setId(categoryId);
        savedCategoryDTO.setName("Updated Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findAll()).thenReturn(List.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDTO.class)).thenReturn(savedCategoryDTO);

        CategoryDTO result = categoryService.updateCategory(categoryId, categoryDTO);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Updated Electronics", result.getName());

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findAll();
        verify(categoryRepository).save(existingCategory);
        verify(modelMapper).map(savedCategory, CategoryDTO.class);
    }

    @Test
    void testUpdateCategory_CategoryNotFound() {
        Long categoryId = 999L;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_NameConflict() {
        Long categoryId = 1L;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Clothing");

        Category existingCategory = new Category();
        Category anotherCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Electronics");
        anotherCategory.setId(2L);
        anotherCategory.setName("clothing");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findAll()).thenReturn(List.of(existingCategory, anotherCategory));

        assertThrows(ResourceConflictException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findAll();
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
