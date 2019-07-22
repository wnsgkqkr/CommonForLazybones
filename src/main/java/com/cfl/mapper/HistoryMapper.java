package com.cfl.mapper;

import com.cfl.domain.History;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HistoryMapper {
    void insertHistory(@Param("history") History history);
}
