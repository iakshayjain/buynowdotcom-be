package com.dailycodework.buynowdotcom.service.image;

import com.dailycodework.buynowdotcom.dtos.ProductDTO;
import com.dailycodework.buynowdotcom.model.Image;
import com.dailycodework.buynowdotcom.model.Product;
import com.dailycodework.buynowdotcom.repository.ImageRepository;
import com.dailycodework.buynowdotcom.dtos.ImageDTO;
import com.dailycodework.buynowdotcom.service.product.IProductService;
import com.dailycodework.buynowdotcom.utils.MappingUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private final ImageRepository imageRepository;

    private final IProductService productService;

    private final MappingUtil mapper = new MappingUtil();

    private final ModelMapper modelMapper;

    /**
     * @return all images
     */
    @Override
    public List<ImageDTO> getAllImages() {
        return imageRepository
                .findAll()
                .stream()
                .map(image -> (ImageDTO) mapper.convertRequestToEntity(image, new ImageDTO()))
                .toList();
    }

    /**
     * @param id existing image id
     * @return image
     */
    @Override
    public Image findImageById(Long id) {
        return imageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found!"));
    }

    /**
     * @param fileName existing image file name
     * @return image
     */
    @Override
    public Image findImageByFileName(String fileName) {
        return imageRepository
                .findByFileName(fileName)
                .orElseThrow(() -> new EntityNotFoundException("Image not found!"));
    }

    /**
     * @param id existing image id
     * @param file image file
     * @return image
     */
    @Override
    public ImageDTO updateImage(Long id, @NonNull MultipartFile file) {
        return imageRepository.findById(id).map(image -> {
            Image updatedImage = (Image) mapper.convertRequestToEntity(file, image);
            imageRepository.save(updatedImage);
            return (ImageDTO) mapper.convertRequestToEntity(updatedImage, new ImageDTO());
        }).orElseThrow(() -> new EntityNotFoundException("Image not found!"));
    }

    /**
     * @param files image file list
     * @return image response list
     */
    @Override
    public List<ImageDTO> saveImages(Long productId, List<MultipartFile> files) {
        List<ImageDTO> responses = new ArrayList<>();
        ProductDTO productDTO = productService.getProductById(productId);
        Product product = modelMapper.map(productDTO, Product.class);
        files.forEach(file -> {

            Image image = (Image) mapper.convertRequestToEntity(file, new Image());
            image.setProduct(product);
            Image savedImage = imageRepository.save(image);

            ImageDTO response = modelMapper.map(savedImage, ImageDTO.class);
            responses.add(response);
        });
        return responses;
    }

    /**
     * @param id existing image id
     */
    @Override
    public void deleteImage(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository :: delete,
                () -> {
                    throw new EntityNotFoundException("Image not found!");
                });
    }
}
