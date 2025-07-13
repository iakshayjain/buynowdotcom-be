package com.dailycodework.buynowdotcom.service.category;

import com.dailycodework.buynowdotcom.dtos.CategoryDTO;
import com.dailycodework.buynowdotcom.model.Category;
import com.dailycodework.buynowdotcom.repository.CategoryRepository;
import com.dailycodework.buynowdotcom.request.CategoryRequest;
import com.dailycodework.buynowdotcom.utils.MappingUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    private MappingUtil mapper = new MappingUtil();

    /**
     * @return all categories
     */
    @Override
    public List<CategoryDTO> getAllCategories() {
        var categories = categoryRepository.findAll();
        return categories
                .stream()
                .filter(Objects::nonNull)
                .map(category -> (CategoryDTO) mapper.convertRequestToEntity(category, new CategoryDTO())).toList();
    }

    /**
     * @param categoryId existing category id
     * @return category
     */
    @Override
    public CategoryDTO findCategoryById(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .map(category -> (CategoryDTO) mapper.convertRequestToEntity(category, new CategoryDTO()))
                .orElseThrow(() -> new EntityNotFoundException("Category not found!"));
    }

    /**
     * @param name existing category name
     * @return category
     */
    @Override
    public CategoryDTO findCategoryByName(String name) {
        return Optional.ofNullable(categoryRepository.findByName(name))
                .map(category -> (CategoryDTO) mapper.convertRequestToEntity(category, new CategoryDTO()))
                .orElseThrow(() -> new EntityNotFoundException("Category not found!"));
    }

    /**
     * @param request new category request
     * @return category
     */
    @Override
    public CategoryDTO addCategory(@NonNull CategoryRequest request) {
        if (isCategoryExists(request.getName())) {
            throw new EntityExistsException("Category already exists");
        }

        Category category = (Category) mapper.convertRequestToEntity(request, new Category());
        Category savedCategory = categoryRepository.save(category);
        return (CategoryDTO) mapper.convertRequestToEntity(savedCategory, new CategoryDTO());
    }

    /**
     * @param categoryId existing category id
     * @param updateRequest update category request
     * @return category
     */
    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryRequest updateRequest) {
        return categoryRepository.findById(categoryId).map(existingCategory -> {
            Optional.of(updateRequest.getName()).ifPresent(name -> {
                existingCategory.setName(name);
                categoryRepository.save(existingCategory);
            });
            return (CategoryDTO) mapper.convertRequestToEntity(existingCategory, new CategoryDTO());
        }).orElseThrow(() -> new EntityNotFoundException("Category not found!"));
    }

    /**
     * @param categoryId existing category id
     */
    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId).ifPresentOrElse(categoryRepository :: delete,
        () -> {
            throw new EntityNotFoundException("Category not found!");
        });
    }

    /**
     * Check if category already exists in db
     * @param name category name
     */
    private boolean isCategoryExists(String name) {
        return categoryRepository.existsByName(name);
    }
}
