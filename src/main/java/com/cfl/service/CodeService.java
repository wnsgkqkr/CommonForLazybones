package com.cfl.service;

import com.cfl.mapper.CodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService {
    @Autowired
    CodeMapper codeMapper;
}
