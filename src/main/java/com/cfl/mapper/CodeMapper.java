package com.cfl.mapper;

import com.cfl.domain.Code;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CodeMapper {
    void insertCode(Code code);
    void insertCodeMultiLanguage(String multiLanguageCode, Map<String, String> multiLanguageMap);

    void updateCode(Code code);

    void deleteCode(Code code);
    void deleteCodeMultiLanguage(String multiLanguageCode);

    Code selectCode(Code code);

    List<Code> selectAllCodes();
    List<Code> selectServiceCodes(String serviceName);
    List<Code> selectTenantCodes(String serviceName, String tenantId);

    List<Map<String, String>> selectCodeMultiLanguageMapList(String serviceName, String tenantId);
}
