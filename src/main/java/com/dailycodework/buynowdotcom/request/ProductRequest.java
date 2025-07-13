package com.dailycodework.buynowdotcom.request;

import com.dailycodework.buynowdotcom.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private int inventory;
    private Category category;
}
