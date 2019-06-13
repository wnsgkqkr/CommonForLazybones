package com.cfl.mapper;

import com.cfl.domain.CflObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CflObjectMapper {
    void insertObject(CflObject object);
    void updateObject(String serviceName, String tenantId, String objectId, @Param("object")CflObject object);
    void deleteObject(CflObject object);
    CflObject selectObject(CflObject object);

    List<CflObject> selectAllObjects();
    List<CflObject> selectServiceObjects(String serviceName);
    List<CflObject> selectTenantObjects(String serviceName, String tenantId);
}
