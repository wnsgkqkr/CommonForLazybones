package com.cfl.service;

import com.cfl.domain.AllowedServer;
import com.cfl.domain.ApiRequest;
import com.cfl.mapper.AllowedServerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public AllowedServer createData(ApiRequest requestObject){
        AllowedServer allowedServer = setAllowedServer(requestObject);
        allowedServerMapper.insertAllowedServer(allowedServer);
        return allowedServer;
    }
    public AllowedServer modifyData(ApiRequest requestObject){
        String originalIp = requestObject.getOriginalIp();
        AllowedServer allowedServer = setAllowedServer(requestObject);
        allowedServerMapper.updateAllowedServer(allowedServer, originalIp);
        return allowedServer;
    }
    public AllowedServer removeData(ApiRequest requestObject){
        AllowedServer allowedServer = setAllowedServer(requestObject);
        allowedServerMapper.deleteAllowedServer(allowedServer);
        return allowedServer;
    }
    public AllowedServer getData(ApiRequest requestObject){
        return allowedServerMapper.selectAllowedServer(setAllowedServer(requestObject));
    }
    //VO request to AllowedServer Object
    public AllowedServer setAllowedServer(ApiRequest requestObject){
        AllowedServer allowedServer = requestObject.getAllowedServer();
        allowedServer.setServiceName(requestObject.getServiceName());
        allowedServer.setTenantId(requestObject.getTenantId());

        return allowedServer;
    }
}
