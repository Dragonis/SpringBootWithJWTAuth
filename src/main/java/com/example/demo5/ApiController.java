package com.example.demo5;

import io.jsonwebtoken.*;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.SignatureException;

@RestController
public class ApiController {

    private final String secretKey = "secretKey";

    /* Maps to all HTTP actions by default (GET,POST,..)*/
    @RequestMapping("/users")
    public @ResponseBody
    String getUsers() {
        return "{\"users\":[{\"firstname\":\"Richard\", \"lastname\":\"Feynman\"}," +
                "{\"firstname\":\"Marie\",\"lastname\":\"Curie\"}]}";
    }

    @GetMapping("/users/{header}/{payload}/{signature}")
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

            String username = claims.get("username").toString();
            String password = claims.get("username").toString();
            return "Username: " + username + ", Password: " + password;

        }catch(Exception ex)
        {
            return ex.getMessage();
        }

    }


}
