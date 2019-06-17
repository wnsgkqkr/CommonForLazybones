package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Code {
    private String codeId;
    private String codeName;
    private String parentCodeId;
    private String codeDescription;
    private int sortOrder;
    private boolean isUsed;
    private String multiLanguageCode;
    private String serviceName;
    private String tenantId;
    private Map<String, Code> subCodes; // Todo subCodes 추가?
}
