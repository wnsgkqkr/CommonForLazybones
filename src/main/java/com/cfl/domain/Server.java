package com.cfl.domain;

import com.cfl.util.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Server {
    private String serverIp;
    private String serverName;
    private String tenantId;
    private String serviceName;

    public Server() { }

    public Server(String serviceName, String tenantId) {
        this.serviceName = serviceName;

        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }

    public Server(String serviceName, String tenantId, String serverIp) {
        this(serviceName, tenantId);
        this.serverIp = serverIp;
    }

    public void setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }
}
