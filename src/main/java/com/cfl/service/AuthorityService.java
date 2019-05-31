package com.cfl.service;

import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {
    @Autowired
    AuthorityMapper authorityMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CflObjectMapper cflObjectMapper;

}
