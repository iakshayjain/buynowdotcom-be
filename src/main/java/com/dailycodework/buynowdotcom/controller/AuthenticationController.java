package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.request.LoginRequest;
import com.dailycodework.buynowdotcom.security.jwt.JwtUtils;
import com.dailycodework.buynowdotcom.security.shopuser.ShopUserDetailsService;
import com.dailycodework.buynowdotcom.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/authentication")
public class AuthenticationController {

    private final ShopUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refreshToken.expirationInMilliseconds}")
    private String refreshTokenExpirationTime;

    @PostMapping("/token")
    @Operation(summary = "Generate JWT Token", description = "Authentication using JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully")
    })
    public ResponseEntity<?> generateToken(@RequestBody LoginRequest loginRequest,
                                           HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDetails, loginRequest.getPassword(), userDetails.getAuthorities()));
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getEmail());
        cookieUtil.addRefreshTokenToCookie(response, refreshToken, Long.parseLong(refreshTokenExpirationTime));
        if(accessToken != null) {
            Map<String, String> token = new HashMap<>();
            token.put("access_token", accessToken);
            return ResponseEntity
                    .ok()
                    .body(token);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred while generating access token");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateRefreshToken(HttpServletRequest request) {
        cookieUtil.printCookieLogs(request);
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);
        if(refreshToken != null && jwtUtils.validateToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
            String accessToken = jwtUtils.generateAccessToken(authentication);
            if(accessToken != null) {
                Map<String, String> token = new HashMap<>();
                token.put("access_token", accessToken);
                return ResponseEntity
                        .ok()
                        .body(token);
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while generating refresh token");
        }
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("invalid or expired token");
    }
}
