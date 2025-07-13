package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.request.CategoryRequest;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/category")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(categoryService.getAllCategories(), false));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(categoryService.findCategoryById(id), false));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name) {
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(categoryService.findCategoryByName(name), false));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse(categoryService.addCategory(request), false));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity
                .status(ACCEPTED)
                .body(new ApiResponse(categoryService.updateCategory(id, request), false));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .status(NO_CONTENT)
                .body(new ApiResponse("Record deleted", false));
    }
}
