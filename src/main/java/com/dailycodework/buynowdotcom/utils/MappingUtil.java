package com.dailycodework.buynowdotcom.utils;

import com.dailycodework.buynowdotcom.model.Image;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MappingUtil {

    public Object convertRequestToEntity(Object request, Object entity) {

        if(request instanceof MultipartFile file) {
            Image image = (Image) entity;

            try {
                Optional.ofNullable(file.getOriginalFilename()).ifPresent(image :: setFileName);
                Optional.ofNullable(file.getContentType()).ifPresent(image :: setFileType);
                Optional.of(new SerialBlob(file.getBytes())).ifPresent(image :: setImage);
                Optional.ofNullable(file.getOriginalFilename()).ifPresent(fileName -> image.setDownloadUrl("/api/v1/images/download/" + fileName));
                return image;
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String input = objectMapper.writer().writeValueAsString(request);
            return objectMapper.readValue(input, entity.getClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
