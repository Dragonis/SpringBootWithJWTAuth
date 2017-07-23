package com.example.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class GenerateJWTKTokenService {

    private final String secretKey = "secretKey";

    /*@Value("${jwt.liveTokenInSeconds}")*/
    private long liveTokenInSeconds = 10;

    public String generateToken(String document_id, String username, String password) {
        Date expiration = Date.from(LocalDateTime.now().plusSeconds(1).toInstant(ZoneOffset.UTC));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long ttlMillis = liveTokenInSeconds * 1000; // how many seconds token has been available x secunds = x * 1000 (ms)

        JwtBuilder jwtBuilder = null;
        try {
            jwtBuilder = Jwts.builder()
                        .setIssuedAt(Date.from(Instant.now()))
                        .setExpiration(expiration)
                        .claim("document_id", document_id)
                        .claim("username", username)
                        .claim("password", password)
                        .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            jwtBuilder.setExpiration(exp);
        }
        String generatedToken = jwtBuilder.compact().replace(".", "/");

        return generatedToken;
    }

    public String receiveToken(String header, String payload, String signature)
       {

            try {
                String token = header + "." + payload + "." + signature;

                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey.getBytes("UTF-8"))
                        .parseClaimsJws(token)
                        .getBody();

                String document_id = claims.get("document_id").toString();
                String username = claims.get("username").toString();
                String password = claims.get("password").toString();

                String output = "Document_id: " + document_id + " <br> " +
                        "Username: " + username + ",<br>" +
                        "Password: " + password;
                return output;
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }

        public String getUsers(){
            return "{\"users\":[{\"firstname\":\"Richard\", \"lastname\":\"Feynman\"}," +
                    "{\"firstname\":\"Marie\",\"lastname\":\"Curie\"}]}";
        }
    }
