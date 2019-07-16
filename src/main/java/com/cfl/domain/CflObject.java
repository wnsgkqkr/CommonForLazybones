package com.cfl.domain;

import com.cfl.util.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CflObject {
    private long objectSequence;
    private String objectId;
    private String objectName;
    private String objectType;
    private String tenantId;
    private String serviceName;
    private Map<String, CflObject> subObjects;
    private List<Authority> authorities;

    public CflObject() { }

    public CflObject(String serviceName, String tenantId) {
        this.serviceName = serviceName;

        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }

    public CflObject(String serviceName, String tenantId, String objectId) {
        this(serviceName, tenantId);
        this.objectId = objectId;
    }

    public void setTenantId(String tenantId) {
        if (tenantId == null) {
            this.tenantId = Constant.DEFAULT_TENANT_ID;
        } else {
            this.tenantId = tenantId;
        }
    }
}
