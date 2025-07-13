package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.request.ProductRequest;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getAllProducts(), false));
    }

    @GetMapping("/get/id/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getProductById(productId), false));
    }

    @GetMapping("/get/category/{category}/brand/{brand}")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(@PathVariable String category, @PathVariable String brand) {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getProductsByCategoryAndBrand(category, brand), false));
    }

    @GetMapping("/get/product/{product}/brand/{brand}")
    public ResponseEntity<ApiResponse> getProductsByProductNameAndBrand(@PathVariable String product, @PathVariable String brand) {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getProductsByProductNameAndBrand(product, brand), false));
    }

    @GetMapping("/get/product/{product}")
    public ResponseEntity<ApiResponse> getProductsByProductName(@PathVariable String product) {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getProductsByProductName(product), false));
    }

    @GetMapping("/get/category/{category}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getProductsByCategory(category), false));
    }

    @GetMapping("/get/brand/{brand}")
    public ResponseEntity<ApiResponse> getProductsByBrand(@PathVariable String brand) {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getProductsByBrand(brand), false));
    }

    @GetMapping("/get/distinct")
    public ResponseEntity<ApiResponse> getDistinctProductsByName() {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getDistinctProductsByName(), false));
    }

    @GetMapping("/get/distinct/brands")
    public ResponseEntity<ApiResponse> getDistinctBrands() {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(productService.getDistinctBrands(), false));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody ProductRequest request) {
        return ResponseEntity
                .accepted()
                .body(new ApiResponse(productService.addProduct(request), false));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity
                .accepted()
                .body(new ApiResponse(productService.updateProduct(id, request), false));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity
                .accepted()
                .body(new ApiResponse("Product with id: " + id + " is deleted", false));
    }
}
