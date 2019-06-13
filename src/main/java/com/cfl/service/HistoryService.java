package com.cfl.service;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.History;
import com.cfl.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class HistoryService {
    @Autowired
    private HistoryMapper historyMapper;

    public History createHistory(String serviceName, String tenantId, Object requestObject, String returnMessage){
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        History history = setHistory(serviceName, tenantId, requestObject, returnMessage, httpRequest);
        if(!isGetMethod(httpRequest)) {
            historyMapper.insertHistory(history);
        }
        return history;
    }

    private History setHistory(String serviceName, String tenantId, Object requestObject, String returnMessage, HttpServletRequest httpRequest){
        History history = new History();

        history.setRegisterServerIp(httpRequest.getRemoteAddr());
        history.setRequestContents(requestObject.toString());
        history.setRequestMethod(httpRequest.getMethod());
        history.setRequestUrl(httpRequest.getRequestURI());
        history.setReturnMessage(returnMessage);
        history.setServiceName(serviceName);
        history.setTenantId(tenantId);
        return history;
    }

    private Boolean isGetMethod(HttpServletRequest httpRequest){
        if("GET".equals(httpRequest.getMethod())){
            return true;
        }
        return false;
    }
}
