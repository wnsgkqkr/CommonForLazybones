package com.cfl.interceptor;

import com.cfl.customexception.UnauthorizedException;
import com.cfl.service.NetworkService;
import com.cfl.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
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
    private static final Set<String> URL_SET = new HashSet<>(Arrays.asList(URL_VALUES));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            log.info("접근 url : " + request.getRequestURL());
            log.info("pathVarialbe = " + pathVariables);
            if (networkService.isAllowedServer(pathVariables.get("serviceName"), getClientIp(request))) {
                return true;
            }
        } catch (Exception e) {
            log.error(getClientIp(request) + " unauthorized server" ,e);
            throw new UnauthorizedException("unauthorized server");
        }

        log.error(getClientIp(request) + "unauthorized server");
        throw new UnauthorizedException("unauthorized server");
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
