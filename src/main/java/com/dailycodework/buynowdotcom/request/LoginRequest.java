package com.dailycodework.buynowdotcom.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "please provide username")
    private String email;

    @NotBlank(message = "please provide password")
    private String password;
}
