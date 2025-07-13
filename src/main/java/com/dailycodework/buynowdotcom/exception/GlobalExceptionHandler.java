package com.dailycodework.buynowdotcom.exception;

import com.dailycodework.buynowdotcom.response.ApiResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleExceptions(Exception ex) {
        HttpStatus statusCode;

        switch (ex) {
            case EntityExistsException entityExistsException -> statusCode = HttpStatus.CONFLICT;
            case EntityNotFoundException entityNotFoundException -> statusCode = HttpStatus.NOT_FOUND;
            case UsernameNotFoundException usernameNotFoundException -> statusCode = HttpStatus.NOT_FOUND;
            case SQLException sqlException -> statusCode = HttpStatus.SERVICE_UNAVAILABLE;
            case ValidationException validationException -> statusCode = HttpStatus.FORBIDDEN;
            case BadCredentialsException credentialsException -> statusCode = HttpStatus.NOT_FOUND;
            case AuthenticationException authenticationException -> statusCode = HttpStatus.UNAUTHORIZED;
            case CustomException customException -> statusCode = HttpStatus.NOT_FOUND;
            default -> statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(new ApiResponse(ex.getMessage(), true), statusCode);
    }
}
