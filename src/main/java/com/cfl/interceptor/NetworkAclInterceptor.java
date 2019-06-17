package com.cfl.interceptor;

import com.cfl.customexception.UnauthorizedException;
import com.cfl.service.NetworkService;
import com.cfl.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@Slf4j
public class NetworkAclInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private NetworkService networkService;

    private static final String[] URL_VALUES = new String[] {"authority", "user", "object", "network-acl", "code"};
    private static final Set<String> URL_SET = new HashSet<String>(Arrays.asList(URL_VALUES));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> serviceAndTenantId = getServiceAndTenantIdFromUrl(request.getRequestURI());

        if(networkService.isAllowedServer(serviceAndTenantId.get("serviceName"), serviceAndTenantId.get("tenantId"), request.getRemoteAddr())) {
            return true;
        }
        throw new UnauthorizedException("unauthorized server");
    }

    private Map<String, String> getServiceAndTenantIdFromUrl(String url){
        String splitUrlArray[] = url.split("/");
        Map<String, String> serviceAndTenantId = new HashMap<>();

        serviceAndTenantId.put("serviceName", splitUrlArray[1]);
        if(splitUrlArray.length > 1 && !URL_SET.contains(splitUrlArray[2])) {
            serviceAndTenantId.put("tenantId", splitUrlArray[2]);
        } else {
            serviceAndTenantId.put("tenantId", Constant.DEFAULT_TENANT_ID);
        }
        return serviceAndTenantId;
    }
}
