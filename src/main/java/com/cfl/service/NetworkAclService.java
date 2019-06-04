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
public class NetworkAclService {
    @Autowired
    private AllowedServerMapper allowedServerMapper;

    public boolean isAllowedServer(String ipv4Address){
        AllowedServer allowedServer = allowedServerMapper.getAllowedServerByIpv4(ipv4Address);
        //check single IP
        if(allowedServer != null) {
            return true;
        }
        //check regular expression
        List<AllowedServer> regExpServerList = allowedServerMapper.getRegExpServerList();
        for(AllowedServer regExpServer : regExpServerList){
            String regExp = regExpServer.getServerIp();

            if(allowedServer.getServerIp().matches(regExp)){
                return true;
            }
        }
        return false;
    }

    public void insertNetworkAcl(JSONObject requestObject){
        allowedServerMapper.insertAllowedServer(setAllowedServer(requestObject));
        log.info((String)requestObject.getJSONObject("allowedServer").get("serverIp")+" is inserted");
    }
    public void updateNetworkAcl(JSONObject requestObject){
        String originalIp = (String)requestObject.get("originalKey");
        allowedServerMapper.updateAllowedServer(setAllowedServer(requestObject), originalIp);
        log.info((String)requestObject.getJSONObject("allowedServer").get("serverIp")+" is updated");
    }
    public void deleteNetworkAcl(JSONObject requestObject){
        allowedServerMapper.deleteAllowedServer(setAllowedServer(requestObject));
        log.info((String)requestObject.getJSONObject("allowedServer").get("serverIp")+" is deleted");
    }

    public AllowedServer setAllowedServer(JSONObject requestObject){
        //JSON request to AllowedServer Object
        AllowedServer allowedServer = new AllowedServer();
        allowedServer.setServerIp((String)requestObject.getJSONObject("allowedServer").get("serverIp"));
        allowedServer.setServerName((String)requestObject.getJSONObject("allowedServer").get("serverName"));
        allowedServer.setServiceName((String)requestObject.get("serviceName"));
        allowedServer.setTenantId((String)requestObject.get("tenantId"));

        return allowedServer;
    }
}
