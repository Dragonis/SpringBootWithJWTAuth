package com.example.demo5;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.SignatureException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@RestController
public class ApiController {

    private final String secretKey = "secretKey";

    @Value("${jwt.liveTokenInSeconds}")
    private long liveTokenInSeconds;

    /* Maps to all HTTP actions by default (GET,POST,..)*/
    @RequestMapping("/users")
    public @ResponseBody
    String getUsers() {
        return "{\"users\":[{\"firstname\":\"Richard\", \"lastname\":\"Feynman\"}," +
                "{\"firstname\":\"Marie\",\"lastname\":\"Curie\"}]}";
    }


    @GetMapping("/token/generate/{document_id}/{username}/{password}")
    public HttpEntity<String> getGeneratedToken(
            @PathVariable("document_id") String document_id,
            @PathVariable("username") String username,
            @PathVariable("password") String password
    ) throws UnknownHostException, UnsupportedEncodingException, MalformedURLException, URISyntaxException {


        Date expiration = Date.from(LocalDateTime.now().plusSeconds(1).toInstant(ZoneOffset.UTC));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long ttlMillis = liveTokenInSeconds * 1000; // how many seconds token has been available x secunds = x * 1000 (ms)

        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(expiration)
                .claim("document_id", document_id)
                .claim("username", username)
                .claim("password", password)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"));


        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            jwtBuilder.setExpiration(exp);
        }
        String generatedToken = jwtBuilder.compact();

        generatedToken = generatedToken.replace(".", "/");

        return new ResponseEntity<String>("http://localhost/download/token/"+ generatedToken, null, HttpStatus.OK);
    }

    @GetMapping("/download/token/{header}/{payload}/{signature}")
    public String getReceiveToken(
                                  @PathVariable("header") String header,
                                  @PathVariable("payload") String payload,
                                  @PathVariable("signature") String signature)
            throws UnknownHostException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, UnsupportedEncodingException {
        try{
            String token = header + "." + payload + "." + signature;


            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();

            String document_id = claims.get("document_id").toString();
            String username = claims.get("username").toString();
            String password = claims.get("password").toString();
            return "Document_id: " + document_id + " <br> " +
                    "Username: " + username + ",<br>" +
                    "Password: " + password;

        }catch(Exception ex)
        {
            return ex.getMessage();
        }

    }


}
