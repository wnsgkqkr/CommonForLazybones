package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CflObject {
    private String objectId;
    private String objectName;
    private String parentObjectId;
    private String tenantId;
    private String serviceName;
    private String objectSequence;
}
