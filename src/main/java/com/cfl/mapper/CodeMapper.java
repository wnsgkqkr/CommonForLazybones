package com.cfl.mapper;

import com.cfl.domain.Code;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeMapper {
    void insertCode(Code code);
    void updateCode(String serviceName, String tenantId, String codeId, @Param("code") Code code);
    void deleteCode(Code code);
    Code selectCode(Code code);

    List<Code> selectAllCodes();
    List<Code> selectServiceCodes(String serviceName);
    List<Code> selectTenantCodes(String serviceName, String tenantId);
}
