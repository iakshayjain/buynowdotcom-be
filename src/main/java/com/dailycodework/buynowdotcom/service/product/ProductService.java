package com.dailycodework.buynowdotcom.service.product;

import com.dailycodework.buynowdotcom.dtos.CategoryDTO;
import com.dailycodework.buynowdotcom.dtos.ImageDTO;
import com.dailycodework.buynowdotcom.dtos.ProductDTO;
import com.dailycodework.buynowdotcom.model.Category;
import com.dailycodework.buynowdotcom.model.Image;
import com.dailycodework.buynowdotcom.model.Product;
import com.dailycodework.buynowdotcom.repository.*;
import com.dailycodework.buynowdotcom.request.ProductRequest;
import com.dailycodework.buynowdotcom.utils.MappingUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final OrderItemRepository orderItemRepository;

    private final ImageRepository imageRepository;

    private final CartItemRepository cartItemRepository;

    private final MappingUtil mapper = new MappingUtil();

    private final ModelMapper modelMapper;

    /**
     * @param request Add Product Request
     * @return Product
     */
    @Override
    public ProductDTO addProduct(ProductRequest request) {
        if(productExists(request.getName(), request.getBrand())) {
            throw new EntityExistsException(request.getName() + " already exists!");
        }

        Category category = getCategory(request);

        Product product = createProduct(request, category);
        Product savedProduct = productRepository.save(product);
        return getProductDTO(savedProduct);
    }

    /**
     * @param productId existing product id
     * @param updateRequest update product details
     * @return Product
     */
    @Override
    public ProductDTO updateProduct(Long productId, ProductRequest updateRequest) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, updateRequest))
                .map(productRepository :: save)
                .map(this::getProductDTO)
                .orElseThrow(() -> new EntityNotFoundException("Product not found!"));
    }

    /**
     * @param productId existing product id
     * @return Product
     */
    @Override
    public ProductDTO getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(this::getProductDTO)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    /**
     * @param productId existing product id
     */
    @Override
    public void deleteProductById(Long productId) {
        productRepository.findById(productId).ifPresentOrElse(product -> {
            categoryRepository.findById(product.getCategory().getId())
                    .ifPresentOrElse(category -> category.getProducts().remove(product),
                            () -> {
                                throw new EntityNotFoundException("Category not found for Product ID: " + productId);
                            });

            product.setCategory(null);

            Optional.ofNullable(cartItemRepository.findByProductId(productId))
                    .ifPresent(cartItemRepository::delete);
            Optional.ofNullable(orderItemRepository.findByProductId(productId))
                    .ifPresent(orderItemRepository::delete);
            Optional.ofNullable(imageRepository.findByProductId(productId))
                    .ifPresent(images -> images.forEach(imageRepository::delete));
            productRepository.delete(product);
        }, () -> {
            throw new EntityNotFoundException("Product not found!");
        });
    }

    /**
     * @return all products
     */
    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::getProductDTO).toList();
    }

    /**
     * @param category category name
     * @param brand brand name
     * @return Product List
     */
    @Override
    public List<ProductDTO> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand).stream().map(this::getProductDTO).toList();
    }

    /**
     * @param productName product name
     * @param brand brand name
     * @return Product List
     */
    @Override
    public List<ProductDTO> getProductsByProductNameAndBrand(String productName, String brand) {
        return productRepository.findByNameAndBrand(productName, brand).stream().map(this::getProductDTO).toList();
    }

    /**
     * @param productName product name
     * @return Product List
     */
    @Override
    public List<ProductDTO> getProductsByProductName(String productName) {
        return productRepository.findByName(productName).stream().map(this::getProductDTO).toList();
    }

    /**
     * @param category category name
     * @return Product List
     */
    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category).stream().map(this::getProductDTO).toList();
    }

    /**
     * @param brand brand name
     * @return Product List
     */
    @Override
    public List<ProductDTO> getProductsByBrand(String brand) {

        CompletableFuture<List<Product>> futureProducts = productRepository.findByBrand(brand);

        CompletableFuture<List<ProductDTO>> futureProductDTOs = futureProducts
                .thenApply(this::getAsyncProductDTOs)
                .exceptionally(ex -> {
                    System.err.println("Error: " + ex.getMessage());
                    return null;
                });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("Is async process done? {}", futureProductDTOs.isDone());

        return futureProductDTOs.resultNow();
    }

    /**
     * @return List of distinct products filtered by name
     */
    @Override
    public List<ProductDTO> getDistinctProductsByName() {
        List<ProductDTO> productDTOS = getAllProducts();
        Map<String, ProductDTO> productDTOMap = productDTOS
                .stream()
                .collect(Collectors.toMap(ProductDTO::getName, productDTO -> productDTO, (existing, replacement) -> existing));
        return productDTOMap.values().stream().toList();
    }

    /**
     * @return List of distinct brands
     */
    @Override
    public List<String> getDistinctBrands() {
        return getAllProducts().stream().map(ProductDTO::getBrand).distinct().toList();
    }

    private boolean productExists(String productName, String brand) {
        return productRepository.existsByNameAndBrand(productName, brand);
    }

    private Product createProduct(ProductRequest request, Category category) {
        Product product = (Product) mapper.convertRequestToEntity(request, new Product());
        product.setCategory(category);
        return product;
    }

    private Category getCategory(ProductRequest request) {
        return Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> categoryRepository.save(request.getCategory()));
    }

    private Product updateExistingProduct(Product existingProduct, ProductRequest updateRequest) {
        if(Objects.nonNull(updateRequest)) {
            Optional.ofNullable(updateRequest.getName()).ifPresent(existingProduct::setName);
            Optional.ofNullable(updateRequest.getBrand()).ifPresent(existingProduct::setBrand);
            Optional.ofNullable(updateRequest.getDescription()).ifPresent(existingProduct::setDescription);
            Optional.ofNullable(updateRequest.getPrice()).ifPresent(existingProduct::setPrice);
            Optional.of(updateRequest.getInventory()).ifPresent(existingProduct::setInventory);
            Optional.ofNullable(updateRequest.getCategory().getName())
                    .ifPresent(categoryName -> existingProduct.setCategory(getCategory(updateRequest)));
        }
        return existingProduct;
    }

    private ProductDTO getProductDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDTO> imageDTOS = images.stream().map(image -> modelMapper.map(image, ImageDTO.class)).toList();
        productDTO.setImages(imageDTOS);
        Category category = product.getCategory();
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        productDTO.setCategory(categoryDTO);
        return productDTO;
    }

    private List<ProductDTO> getAsyncProductDTOs(List<Product> products) {

        return products.stream().map((product) -> ProductDTO
                .builder()
                .name(product.getName())
                .brand(product.getBrand())
                .id(product.getId())
                .description(product.getDescription())
                .category(CategoryDTO
                        .builder()
                        .name(product.getCategory().getName())
                        .id(product.getCategory().getId())
                        .build())
                .price(product.getPrice())
                .images(product.getImages().stream().map(image -> ImageDTO
                        .builder()
                        .id(image.getId())
                        .fileName(image.getFileName())
                        .downloadUrl(image.getDownloadUrl())
                        .fileType(image.getFileType())
                        .build()).toList())
                .build()).toList();
    }
}
