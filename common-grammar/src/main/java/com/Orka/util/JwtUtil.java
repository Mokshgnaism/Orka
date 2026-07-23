package com.Orka.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
@Component
public class JwtUtil {
    private final static String SECRET = "my-strong-secret-key";
    private final static SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
//            will be used from envs donot worry
    public  String getJwt(String email,String username){
        return Jwts.builder()
                .claim("username",username)
                .claim("email",email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+864000000))
                .signWith(SECRET_KEY)
                .compact();
    }

    public  Claims getClaims(String jwt){
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(jwt).getPayload();
    }

    public  String getUsername(String jwt){
        return getClaims(jwt).get("username",String.class);
    }
    public  String getEmail(String jwt){
        return getClaims(jwt).get("email",String.class);
    }
}
