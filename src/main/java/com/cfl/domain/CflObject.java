package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CflObject {
    private String objectId;
    private String objectName;
    private Map<String, CflObject> parentObjects;
    private String tenantId;
    private String serviceName;
    private String objectSequence;
    private Map<String, CflObject> subObjects;
    private List<String> authorityIds;
}
