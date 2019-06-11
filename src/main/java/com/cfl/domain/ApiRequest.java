package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiRequest {
    String serviceName;
    String tenantId;
    String compareType;
    String originalIp;

    CflObject object;
    Authority authority;
    User user;
    AllowedServer allowedServer;
    Code code;
    History history;
}
