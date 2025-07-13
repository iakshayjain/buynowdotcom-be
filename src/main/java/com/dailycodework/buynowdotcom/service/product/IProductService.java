package com.dailycodework.buynowdotcom.service.product;

import com.dailycodework.buynowdotcom.dtos.ProductDTO;
import com.dailycodework.buynowdotcom.request.ProductRequest;

import java.util.List;

public interface IProductService {
    ProductDTO addProduct(ProductRequest product);
    ProductDTO updateProduct(Long productId, ProductRequest product);
    ProductDTO getProductById(Long productId);
    void deleteProductById(Long productId);

    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategoryAndBrand(String category, String brand);
    List<ProductDTO> getProductsByProductNameAndBrand(String productName, String brand);
    List<ProductDTO> getProductsByProductName(String productName);
    List<ProductDTO> getProductsByCategory(String category);
    List<ProductDTO> getProductsByBrand(String brand);

    List<ProductDTO> getDistinctProductsByName();

    List<String> getDistinctBrands();
}
