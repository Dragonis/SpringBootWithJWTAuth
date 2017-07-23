package com.example.demo5;

import com.example.services.GenerateJWTKTokenService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    private GenerateJWTKTokenService generateJWTKTokenService = new GenerateJWTKTokenService();

    @RequestMapping("/users")
    public @ResponseBody
    String getUsers() {
        return generateJWTKTokenService.getUsers();
    }


    @GetMapping("/token/generate/{document_id}/{username}/{password}")
    public HttpEntity<String> getGeneratedToken(
            @PathVariable("document_id") String document_id,
            @PathVariable("username") String username,
            @PathVariable("password") String password
    ){

        String url = "http://localhost/download/token/";
        String generatedToken = generateJWTKTokenService.generateToken(document_id,username,password);

        return new ResponseEntity<String>(url + generatedToken, null, HttpStatus.OK);
    }

    @GetMapping("/download/token/{header}/{payload}/{signature}")
    public String getReceiveToken(
                                  @PathVariable("header") String header,
                                  @PathVariable("payload") String payload,
                                  @PathVariable("signature") String signature)
    {
        return generateJWTKTokenService.receiveToken(header,payload,signature);
    }


}
