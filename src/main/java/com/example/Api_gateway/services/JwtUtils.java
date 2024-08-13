package com.example.Api_gateway.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtils {
    private final String secretKey = "ServiceAuthToken423AS34KS03S3CR3D0QW334SDFASDFQ34RW345EW4RTFSE4T5RE45TERTWE45TWE45WERTW345WERTFW45TWE4TWE45TW3";

    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isExpired(String token){
       try{
           return getClaims(token).getExpiration().before(new Date());
       }catch (Exception e){
           return true;
       }
    }
    public Integer extractUserId(String token){
        try{
            return Integer.parseInt(getClaims(token).getSubject());
        }catch (Exception e){
            return null;
        }
    }
}
