package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Authority {
    private String authorityId;
    private String authorityName;
    private String authorityType;
    private String tenantId;
    private String serviceName;
    private String authoritySequence;

    private Map<String, User> authorityToUsers;
}
