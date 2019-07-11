package com.cfl.domain;

import com.cfl.util.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Authority {
    private String authorityId;
    private String authorityName;
    private String authorityType;
    private String tenantId;
    private String serviceName;
    private String authoritySequence;

    private List<User> authorityToUsers;

    public Authority() { }

    public Authority(String serviceName, String tenantId) {
        this.serviceName = serviceName;

        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }

    public Authority(String serviceName, String tenantId, String authorityId) {
        this(serviceName, tenantId);
        this.authorityId = authorityId;
    }

    public void setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }
}
