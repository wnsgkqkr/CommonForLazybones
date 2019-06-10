package com.cfl.interceptor;

import com.cfl.customexception.UnauthorizedException;
import com.cfl.service.NetworkAclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class NetworkAclInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private NetworkAclService networkAclService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // check request address is in network ACL
        if(networkAclService.isAllowedServer(request.getRemoteAddr())){
            return true;
        }
        throw new UnauthorizedException("unauthorized server");
    }
}
