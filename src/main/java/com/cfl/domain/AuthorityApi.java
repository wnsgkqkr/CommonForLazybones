package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityApi {
    private String serviceName;
    private String tenantId;
    private String compareType;
    private CflObject object;
    private Authority auth;
    private User user;
    private Server allowedServer;
    private Code code;
}
