package com.cfl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CacheUpdateRequest {
    String serviceName;
    String tenantId;
    String cacheType;
}
