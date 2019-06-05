package com.cfl.service;

import com.cfl.domain.Code;
import com.cfl.mapper.CodeMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService implements CflService<Code>{
    @Autowired
    private CodeMapper codeMapper;

    // insert / update / delete / select code from database
    public Code createData(JSONObject requestObject){
        return codeMapper.insertCode(setCode(requestObject));
    }
    public Code modifyData(JSONObject requestObject){
        return codeMapper.updateCode(setCode(requestObject));
    }
    public Code removeData(JSONObject requestObject){
        return codeMapper.deleteCode(setCode(requestObject));
    }
    public Code getData(JSONObject requestObject){
        return codeMapper.selectCode(setCode(requestObject));
    }
    //JSON request to Code Object
    public Code setCode(JSONObject requestObject){
        Code code = new Code();
        code.setCodeId((String)requestObject.getJSONObject("code").get("codeId"));
        code.setCodeName((String)requestObject.getJSONObject("code").get("codeName"));
        code.setCodeDescription((String)requestObject.getJSONObject("code").get("codeDescription"));
        code.setMultiLanguageCode((String)requestObject.getJSONObject("code").get("multiLanguageCode"));
        code.setParentCodeId((String)requestObject.getJSONObject("code").get("parentCodeId"));
        code.setSortOrder((int)requestObject.getJSONObject("code").get("sortOrder"));
        code.setUsed((boolean)requestObject.getJSONObject("code").get("isUsed"));
        code.setServiceName((String)requestObject.get("serviceName"));
        code.setTenantId((String)requestObject.get("tenantId"));

        return code;
    }
}
