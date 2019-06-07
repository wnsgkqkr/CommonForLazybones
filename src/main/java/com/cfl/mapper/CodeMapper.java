package com.cfl.mapper;

import com.cfl.domain.Code;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeMapper {
    void insertCode(Code code);
    void updateCode(Code code);
    void deleteCode(Code code);
    Code selectCode(Code code);
}
