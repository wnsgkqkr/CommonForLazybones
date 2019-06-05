package com.cfl.mapper;

import com.cfl.domain.Code;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeMapper {
    Code insertCode(Code code);
    Code updateCode(Code code);
    Code deleteCode(Code code);
    Code selectCode(Code code);
}
