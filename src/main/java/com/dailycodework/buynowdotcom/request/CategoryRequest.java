package com.dailycodework.buynowdotcom.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class CategoryRequest {

    @NonNull
    private String name;
}
