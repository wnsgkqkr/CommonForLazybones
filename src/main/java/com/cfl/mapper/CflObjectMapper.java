package com.cfl.mapper;

import com.cfl.domain.CflObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CflObjectMapper {
    void insertObject(@Param("object")CflObject object);
    void updateObject(@Param("object")CflObject object);
    void deleteObject(@Param("object")CflObject object);
    CflObject selectObject(@Param("object")CflObject object);

    List<CflObject> selectAllObjects();
    List<CflObject> selectServiceObjects(String serviceName);
    List<CflObject> selectTenantObjects(String serviceName, String tenantId);
}
