package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class History {
    String historyId;
    String serviceName;
    String tenantId;
    Date actionDateTime;
    String requestMethod;
    String requestUrl;
    String requestContents;
    String returnMessage;
    String returnContents;
    String requestPerson;
    String registerServerIp;

    public History() {}

    public History(String serviceName, String tenantId, String requestMethod, String requestUrl, String requestContents
                   , String returnMessage, String returnContents, String requestPerson, String registerServerIp) {
        this.serviceName = serviceName;
        this.tenantId = tenantId;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.requestContents = requestContents;
        this.returnMessage = returnMessage;
        this.returnContents = returnContents;
        this.requestPerson = requestPerson;
        this.registerServerIp = registerServerIp;
    }
}
