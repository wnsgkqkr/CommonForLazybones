package com.cfl.domain;

import lombok.Getter;
import lombok.Setter;

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
}
