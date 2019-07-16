package com.cfl.domain;

import com.cfl.util.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {
    private long userSequence;
    private String userId;
    private String userType;
    private String tenantId;
    private String serviceName;
    private List<Authority> userToAuthorities;

    public User() { }

    public User(String serviceName, String tenantId) {
        this.serviceName = serviceName;

        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }

    public User(String serviceName, String tenantId, String userId) {
        this(serviceName, tenantId);
        this.userId = userId;
    }

    public void setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }
}
