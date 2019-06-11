package com.cfl.service;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.History;
import com.cfl.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class HistoryService {
    @Autowired
    private HistoryMapper historyMapper;

    public History createHistory(String objectMethod, ApiRequest request, String returnMessage){
        History history = setHistory(objectMethod, request, returnMessage);
        historyMapper.insertHistory(history);
        return history;
    }

    public History setHistory(String objectMethod, ApiRequest request, String returnMessage){
        History history = new History();
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        history.setRegisterServerIp(httpRequest.getRemoteAddr());
        history.setHistoryId(UUID.randomUUID().toString());
        history.setActionDateTime(new Timestamp(new Date().getTime()));
        history.setRequestContents(httpRequest.getRequestURI() + ' ' + objectMethod);
        history.setReturnMessage(returnMessage);
        history.setServiceName(request.getServiceName());
        history.setServiceName(request.getTenantId());
        return history;
    }
}
