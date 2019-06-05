package com.cfl.service;

import com.cfl.domain.AllowedServer;
import com.cfl.mapper.AllowedServerMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NetworkAclService implements CflService<AllowedServer>{
    @Autowired
    private AllowedServerMapper allowedServerMapper;

    public boolean isAllowedServer(String ipv4Address){
        AllowedServer allowedServer = allowedServerMapper.selectAllowedServerByIpv4(ipv4Address);
        //check single IP
        if(allowedServer != null) {
            return true;
        }
        //check regular expression
        List<AllowedServer> regExpServerList = allowedServerMapper.selectRegExpServerList();
        for(AllowedServer regExpServer : regExpServerList){
            String regExp = regExpServer.getServerIp();

            if(allowedServer.getServerIp().matches(regExp)){
                return true;
            }
        }
        return false;
    }
    // insert / update / delete / select allowedServer from database
    public AllowedServer createData(JSONObject requestObject){
        return allowedServerMapper.insertAllowedServer(setAllowedServer(requestObject));
    }
    public AllowedServer modifyData(JSONObject requestObject){
        String originalIp = (String)requestObject.get("originalKey");
        return allowedServerMapper.updateAllowedServer(setAllowedServer(requestObject), originalIp);
    }
    public AllowedServer removeData(JSONObject requestObject){
        return allowedServerMapper.deleteAllowedServer(setAllowedServer(requestObject));
    }
    public AllowedServer getData(JSONObject requestObject){
        return allowedServerMapper.selectAllowedServer(setAllowedServer(requestObject));
    }
    //JSON request to AllowedServer Object
    public AllowedServer setAllowedServer(JSONObject requestObject){
        AllowedServer allowedServer = new AllowedServer();
        allowedServer.setServerIp((String)requestObject.getJSONObject("allowedServer").get("serverIp"));
        allowedServer.setServerName((String)requestObject.getJSONObject("allowedServer").get("serverName"));
        allowedServer.setServiceName((String)requestObject.get("serviceName"));
        allowedServer.setTenantId((String)requestObject.get("tenantId"));

        return allowedServer;
    }
}
