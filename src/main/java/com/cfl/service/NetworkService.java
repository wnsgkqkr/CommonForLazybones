package com.cfl.service;

import com.cfl.domain.Server;
import com.cfl.domain.ApiResponse;
import com.cfl.mapper.ServerMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.List;

@Service
@Slf4j
public class NetworkService {
    @Autowired
    private ServerMapper serverMapper;
    @Autowired
    private HistoryService historyService;

    public boolean isAllowedServer(String serviceName, String tenantId, String serverIp) {
        Server server = setAllowedServer(serviceName, tenantId, serverIp, new Server());
        Server getServer = serverMapper.selectAllowedServer(server);
        if(getServer != null) {
            return true;
        }
        //check regular expression
        List<Server> regExpServerList = serverMapper.selectRegExpServerList(serviceName, server.getTenantId());
        for(Server regExpServer : regExpServerList){
            String regExp = regExpServer.getServerIp();

            if(server.getServerIp().matches(regExp)){
                return true;
            }
        }
        return false;
    }

    public ApiResponse createNetworkAcl(String serviceName, String tenantId, String serverIp, Server server) {
        try {
            server = setAllowedServer(serviceName, tenantId, serverIp, server);
            serverMapper.insertAllowedServer(server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, server.getTenantId(), server, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyNetworkAcl(String serviceName, String tenantId, String serverIp, Server server) {
        try {
            server = setAllowedServer(serviceName, tenantId, server.getServerIp(), server);
            serverMapper.updateAllowedServer(serviceName, server.getTenantId(), serverIp, server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, server.getTenantId(), server, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse deleteNetworkAcl(String serviceName, String tenantId, String serverIp) {
        try {
            Server server = setAllowedServer(serviceName, tenantId, serverIp, new Server());
            serverMapper.deleteAllowedServer(server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, server.getTenantId(), server, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getNetworkAcl(String serviceName, String tenantId, String serverIp) {
        try {
            Server server = setAllowedServer(serviceName, tenantId, serverIp, new Server());
            serverMapper.selectAllowedServer(server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, server.getTenantId(), server, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Server setAllowedServer(String serviceName, String tenantId, String serverIp, Server server) {
        server.setServiceName(serviceName);
        if(tenantId == null) {
            server.setTenantId(Constant.DEFAULT_TENANT_ID);
        } else {
            server.setTenantId(tenantId);
        }
        server.setServerIp(serverIp);

        return server;
    }

    @PostConstruct
    private void createProvideServer() {
        Server provideServer = new Server();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            provideServer.setServerIp(ip.getHostAddress());
            provideServer.setServerName(ip.getHostName());
            provideServer.setServiceName("CFL");
            provideServer.setTenantId(Constant.DEFAULT_TENANT_ID);

            if (serverMapper.selectProvideServer(provideServer) == null) {
                serverMapper.insertProvideServer(provideServer);
                serverMapper.insertAllowedServer(provideServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendProvideServersToInit() {
        List<String> provideServerIpList = serverMapper.selectAllProvideServerIp();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        for(String provideServerIp : provideServerIpList) {
            ApiResponse apiResponse = restTemplate.postForObject(provideServerIp+"/init", provideServerIp, ApiResponse.class);
            log.info(apiResponse.toString());
        }
    }
}
