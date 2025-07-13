package com.dailycodework.buynowdotcom.service.category;

import com.dailycodework.buynowdotcom.dtos.CategoryDTO;
import com.dailycodework.buynowdotcom.model.Category;
import com.dailycodework.buynowdotcom.request.CategoryRequest;

import java.util.List;

public interface ICategoryService {

    List<CategoryDTO> getAllCategories();
    CategoryDTO findCategoryById(Long categoryId);
    CategoryDTO findCategoryByName(String name);
    CategoryDTO addCategory(CategoryRequest request);
    CategoryDTO updateCategory(Long categoryId, CategoryRequest updateRequest);
    void deleteCategory(Long categoryId);
}
