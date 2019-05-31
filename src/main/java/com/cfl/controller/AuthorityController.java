package com.cfl.controller;

import com.cfl.domain.AuthorityApi;
import com.cfl.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorityController {
    @Autowired
    AuthorityService authorityService;

    @PostMapping(value="/authority")
    public AuthorityApi sendToResponse(@RequestBody AuthorityApi request, @RequestParam(value="function", required = true)String function){
        
    }
}
