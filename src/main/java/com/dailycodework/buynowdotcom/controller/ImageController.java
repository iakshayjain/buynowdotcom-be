package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.model.Image;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class ImageController {

    private final IImageService imageService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllImages() {
        return ResponseEntity
                .ok()
                .body(new ApiResponse(imageService.getAllImages(), false));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadImages(@RequestParam Long productId,
                                                    @RequestParam List<MultipartFile> files) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ApiResponse(imageService.saveImages(productId, files), false));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) throws SQLException {
        Image image = imageService.findImageByFileName(fileName);
        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1L, (int) image.getImage().length()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateImage(@RequestParam MultipartFile file, @RequestParam Long imageId) {
        return ResponseEntity
                .accepted()
                .body(new ApiResponse(imageService.updateImage(imageId, file), false));
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity
                .accepted()
                .body(new ApiResponse("Image is deleted!", false));
    }
}
