package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.Code;
import com.cfl.service.CodeService;
import com.cfl.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class CodeController {
    @Autowired
    private CodeService codeService;
    @Autowired
    private CommonService commonService;

    //Code create, modify, remove, get
    @RequestMapping(value = "/code")
    public Map<String, Object> createCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.createData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }

    @PutMapping(value = "/code")
    public Map<String, Object> modifyCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }

    @DeleteMapping(value = "/code")
    public Map<String, Object> removeCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }

    @GetMapping(value = "/code")
    public Map<String, Object> getCode(@RequestBody ApiRequest requestObject) {
        try {
            Code code = codeService.getData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(e);
        }
    }
}
