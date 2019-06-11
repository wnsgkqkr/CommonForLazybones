package com.cfl.service;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.Code;
import com.cfl.domain.History;
import com.cfl.mapper.CodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService implements CflService<Code>{
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private HistoryService historyService;

    // insert / update / delete / select code from database
    public Code createData(ApiRequest requestObject){
        Code code = setCode(requestObject);
        codeMapper.insertCode(code);
        historyService.createHistory(code.getCodeName() + " create ", requestObject, "return message");
        return code;
    }
    public Code modifyData(ApiRequest requestObject){
        Code code = setCode(requestObject);
        codeMapper.updateCode(code);
        historyService.createHistory(code.getCodeName() + " modify ", requestObject, "return message");
        return code;
    }
    public Code removeData(ApiRequest requestObject){
        Code code = setCode(requestObject);
        codeMapper.deleteCode(code);
        historyService.createHistory(code.getCodeName() + " remove ", requestObject, "return message");
        return code;
    }
    public Code getData(ApiRequest requestObject){
        return codeMapper.selectCode(setCode(requestObject));
    }
    //VO request to Code Object
    private Code setCode(ApiRequest requestObject){
        Code code = requestObject.getCode();
        code.setServiceName(requestObject.getServiceName());
        code.setTenantId(requestObject.getTenantId());

        return code;
    }
}
