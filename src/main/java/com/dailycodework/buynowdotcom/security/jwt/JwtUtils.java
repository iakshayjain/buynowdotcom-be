package com.dailycodework.buynowdotcom.security.jwt;

import com.dailycodework.buynowdotcom.exception.CustomException;
import com.dailycodework.buynowdotcom.security.shopuser.ShopUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${jwt.accessToken.expirationInMilliseconds}")
    private String accessTokenExpiration;

    @Value("${jwt.refreshToken.expirationInMilliseconds}")
    private String refreshTokenExpiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateAccessToken(Authentication authentication) {
        ShopUserDetails shopUserDetails = (ShopUserDetails) authentication.getPrincipal();
        if(shopUserDetails == null) {
            throw new CustomException("Shop user details not found!");
        }

        List<String> roles = shopUserDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder()
                .subject(shopUserDetails.getEmail())
                .claim("id", shopUserDetails.getId())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(getExpirationTime(accessTokenExpiration))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts
                .builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(getExpirationTime(refreshTokenExpiration))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    private Date getExpirationTime(String expirationTime) {
        var timeInMilliseconds = Long.parseLong(expirationTime);
        return new Date(System.currentTimeMillis() + timeInMilliseconds);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
