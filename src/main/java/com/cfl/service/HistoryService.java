package com.cfl.service;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.History;
import com.cfl.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class HistoryService {
    @Autowired
    private HistoryMapper historyMapper;

    public History createHistory(String objectMethod, ApiRequest request, String returnMessage, HttpServletRequest httpRequest){
        History history = setHistory(objectMethod, request, returnMessage);
        history.setRegisterServerIp(httpRequest.getRemoteAddr());
        historyMapper.insertHistory(history);
        return history;
    }

    public History setHistory(String objectMethod, ApiRequest request, String returnMessage){
        History history = new History();
        history.setActionDateTime(new Date());
        history.setRequestContents(objectMethod + request.toString());
        history.setReturnMessage(returnMessage);
        history.setServiceName(request.getServiceName());
        history.setServiceName(request.getTenantId());
        return history;
    }
}
