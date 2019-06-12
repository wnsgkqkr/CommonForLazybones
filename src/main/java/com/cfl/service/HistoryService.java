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

    public History createHistory(ApiRequest request, String returnMessage){
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        History history = setHistory(request, returnMessage, httpRequest);
        if(!isGetMethod(httpRequest)) {
            historyMapper.insertHistory(history);
        }
        return history;
    }

    private History setHistory(ApiRequest request, String returnMessage, HttpServletRequest httpRequest){
        History history = new History();

        history.setRegisterServerIp(httpRequest.getRemoteAddr());
        history.setRequestContents(httpRequest.getRequestURI() + ' ' + httpRequest.getMethod());
        history.setReturnMessage(returnMessage);
        history.setServiceName(request.getServiceName());
        history.setTenantId(request.getTenantId());
        return history;
    }

    private Boolean isGetMethod(HttpServletRequest httpRequest){
        if("GET".equals(httpRequest.getMethod())){
            return true;
        }
        return false;
    }
}
