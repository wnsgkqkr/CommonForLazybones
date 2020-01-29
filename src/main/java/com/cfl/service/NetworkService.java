package com.cfl.service;

import com.cfl.domain.CacheUpdateRequest;
import com.cfl.domain.Server;
import com.cfl.domain.ApiResponse;
import com.cfl.mapper.ServerMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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

    public boolean isAllowedServer(String serviceName, String serverIp) {
        Server server = new Server(serviceName, serverIp);

        Server getServer = serverMapper.selectAllowedServer(server);
        if (getServer != null) {
            return true;
        }

        //check regular expression
        List<Server> regExpServerList = serverMapper.selectRegExpServerList(serviceName);
        for (Server regExpServer : regExpServerList) {
            String regExp = regExpServer.getServerIp();

            if (server.getServerIp().matches(regExp)) {
                return true;
            }
        }
        return false;
    }

    public ApiResponse createNetworkAcl(String serviceName, String tenantId, String serverIp, Server server) {
        try {
            server.setServiceName(serviceName);
            server.setServerIp(serverIp);

            serverMapper.insertAllowedServer(server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, tenantId, server, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyNetworkAcl(String serviceName, String tenantId, String serverIp, Server server) {
        try {
            server.setServiceName(serviceName);
            server.setServerIp(serverIp);

            serverMapper.updateAllowedServer(server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, tenantId, server, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse deleteNetworkAcl(String serviceName, String tenantId, String serverIp) {
        try {
            Server server = new Server(serviceName, serverIp);

            serverMapper.deleteAllowedServer(server);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(server);
            historyService.createHistory(serviceName, tenantId, server, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getNetworkAcl(String serviceName, String tenantId, String serverIp) {
        try {
            Server server = new Server(serviceName, serverIp);

            serverMapper.selectAllowedServer(server);
            return ApiResponseUtil.getSuccessApiResponse(server);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    private void createProvideServer() {
        Server provideServer = new Server();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            provideServer.setServerIp(ip.getHostAddress());
            provideServer.setServerName(ip.getHostName());
            provideServer.setServiceName("cfl");

            log.info("접속 Ip = " + ip.toString());
            if (serverMapper.selectProvideServerByServerIp(provideServer) == null) {
                serverMapper.insertProvideServer(provideServer);
                serverMapper.insertAllowedServer(provideServer);
                log.info("접속 Ip 등록 완료");
            }
            log.info("접속 Ip 등록 실패");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 해당 서비스들의 캐시를 업데이트하기위해 리퀘스트 온대로 해당 서비스를 제공하는 서버들에게 비동기적으로 api전송 (cfl도 이방식으로 캐시 갱신)
    public ApiResponse sendProvideServersToInit(String serviceName, CacheUpdateRequest cacheUpdateRequest) {
        List<Server> provideServerList = serverMapper.selectProvideServerByServiceName(serviceName);

        for (Server provideServer : provideServerList) {
            String url;
            if (provideServer.getPortNumber() != null) {
                url = "http://" + provideServer.getServerIp() + ":" + provideServer.getPortNumber() + "/" + provideServer.getServiceName() + "/cache/init";
            } else {
                url = "http://" + provideServer.getServerIp() + "/" + provideServer.getServiceName() + "/cache/init";
            }

            WebClient.create(url)
                    .post()
                    .body(BodyInserters.fromObject(cacheUpdateRequest))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(result -> {
                            // 통신 성공시 로그 생성
                            log.info(url + " 캐시 갱신 결과 : " + result);
                        }
                        , error -> {
                            // 통신 실패시 에러 로그 생성
                            log.error(url + " 캐시 갱신 결과 : " + error.getMessage());
                        }
                    );
        }

        return ApiResponseUtil.getSuccessApiResponse("success");
    }
}
