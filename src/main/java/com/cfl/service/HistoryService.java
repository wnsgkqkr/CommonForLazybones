package com.cfl.service;

import com.cfl.domain.ApiResponse;
import com.cfl.domain.History;
import com.cfl.mapper.HistoryMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class HistoryService {
    @Autowired
    private HistoryMapper historyMapper;

    private static Gson gson = new Gson();

    public History createHistory(String serviceName, String tenantId, Object requestObject, ApiResponse response) {

        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String requestMethod = httpRequest.getMethod();
        String requestUrl = httpRequest.getRequestURI();
        String requestContents = gson.toJson(requestObject);
        String returnContents = gson.toJson(response);
        String requestPerson = httpRequest.getHeader("requester");
        String registerServerIp = getClientIp(httpRequest);
        String returnMessage = null;

        if (response != null) {
            returnMessage = response.getHeader().getResultMessage();
        }

        History history = new History(serviceName, tenantId, requestMethod, requestUrl, requestContents, returnMessage, returnContents, requestPerson, registerServerIp);

        historyMapper.insertHistory(history);

        return history;
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-FORWARDED-FOR");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
