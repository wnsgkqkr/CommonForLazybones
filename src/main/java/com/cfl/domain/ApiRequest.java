package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiRequest {
    String compareType;
    String originalIp;
    String requester;
}
