package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class History {
    String historyId;
    String requestContents;
    String returnMessage;
    Date actionDateTime;
    String serviceName;
    String tenantId;
    String registerServerIp;
    String requestMethod;
    String requestUrl;
}
