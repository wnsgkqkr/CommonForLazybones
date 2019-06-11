package com.cfl.mapper;

import com.cfl.domain.History;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoryMapper {
    void insertHistory(History history);
}
