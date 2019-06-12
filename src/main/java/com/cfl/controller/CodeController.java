package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Code;
import com.cfl.service.CodeService;
import com.cfl.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/code")
public class CodeController {
    @Autowired
    private CodeService codeService;
    @Autowired
    private CommonService commonService;

    //Code create, modify, remove, get
    @PostMapping
    public ApiResponse createCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.createData(requestObject);
            return commonService.successResult(code, requestObject);
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }

    @PutMapping
    public ApiResponse modifyCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.modifyData(requestObject);
            return commonService.successResult(code, requestObject);
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }

    @DeleteMapping
    public ApiResponse removeCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.removeData(requestObject);
            return commonService.successResult(code, requestObject);
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }

    @GetMapping
    public ApiResponse getCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.getData(requestObject);
            return commonService.successResult(code, requestObject);
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }
}
