package com.cfl.interceptor;

import com.cfl.service.NetworkAclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class NetworkAclInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private NetworkAclService networkAclService;

    public boolean preHandle(HttpServletRequest request){
        // check request address is in network ACL
        if(networkAclService.isAllowedServer(request.getRemoteAddr())){
            return true;
        }
        log.info(request.getRemoteAddr()+" is Not allowed server");
        return false;
    }
}
