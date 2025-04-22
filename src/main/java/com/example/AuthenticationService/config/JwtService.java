package com.example.AuthenticationService.config;

import com.example.AuthenticationService.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hibernate.engine.jdbc.spi.JdbcWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String key="4c7e6b2d3f8e2c9f1b5c0a7e2f0c6d3a0f7a1d8e5b9c4e2f3a7b1c2d9e5f4a3b";
    public String extractUsername(String token) {
        return extractClaims(token,Claims::getSubject);
    }

    public boolean isTokenValid(String token,UserDetails userDetails)
    {
        final String username=extractUsername(token);
        return  (username.equals(userDetails.getUsername()))  && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user)
    {
        HashMap<String,Object> extraClaimsMap=new HashMap<>();
        extraClaimsMap.put("username",user.getUserIdentity());
        extraClaimsMap.put("userType",user.getRole());
        return generateToken(extraClaimsMap,user);
    }
    public String generateToken(Map<String,Object> extraClaims, User userDetails)
    {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*24*20))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims extractAllClaims(String token)// getting clones
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        // to decode the jwt

    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserId(String token)
    {
        try {
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("username", String.class); // Extract userId
        } catch (Exception e) {
            throw new RuntimeException("Could not extract userId from JWT");
        }
    }
}
