package com.dailycodework.buynowdotcom.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
}
