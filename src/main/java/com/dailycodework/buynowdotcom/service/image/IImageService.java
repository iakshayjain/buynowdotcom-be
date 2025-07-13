package com.dailycodework.buynowdotcom.service.image;

import com.dailycodework.buynowdotcom.model.Image;
import com.dailycodework.buynowdotcom.dtos.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    List<ImageDTO> getAllImages();
    Image findImageById(Long id);
    Image findImageByFileName(String fileName);
    ImageDTO updateImage(Long id, MultipartFile file);
    List<ImageDTO> saveImages(Long productId, List<MultipartFile> files);
    void deleteImage(Long id);
}
