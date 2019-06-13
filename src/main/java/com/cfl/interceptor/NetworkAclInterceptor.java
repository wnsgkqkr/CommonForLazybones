package com.cfl.interceptor;

import com.cfl.customexception.UnauthorizedException;
import com.cfl.service.NetworkService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@Component
@Slf4j
public class NetworkAclInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private NetworkService networkService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        StringBuffer stringBuffer = new StringBuffer();

        try(BufferedReader reader = request.getReader()) {
            String string;
            while ((string = reader.readLine()) != null) {
                stringBuffer.append(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject =  new JSONObject(stringBuffer.toString());

        // check request address is in network ACL
        if(networkService.isAllowedServer((String)jsonObject.get("serviceName"), (String)jsonObject.get("tenantId"), request.getRemoteAddr())) {
            return true;
        }
        throw new UnauthorizedException("unauthorized server");
    }
}
