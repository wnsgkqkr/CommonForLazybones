package com.cfl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AllowedServer {
    private String serverIp;
    private String serverName;
    private String tenantId;
    private String serviceName;
}
