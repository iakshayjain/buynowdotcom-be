package com.dailycodework.buynowdotcom.response;

import lombok.Data;

@Data
public class ApiResponse {

    private String message;
    private Object data;

    private static final String SUCCESS = "Result found!";
    private static final String ERROR = "Error occurred!";

    public ApiResponse(Object data, boolean error) {
        this.message = !error ?  SUCCESS : ERROR;
        this.data = data;
    }
}