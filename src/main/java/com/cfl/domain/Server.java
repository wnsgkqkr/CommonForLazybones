package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Server {
    private String serverIp;
    private String serverName;
    private String tenantId;
    private String serviceName;
}
