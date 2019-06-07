package com.cfl.mapper;

import com.cfl.domain.CflObject;
import org.apache.ibatis.annotations.Mapper;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@Mapper
public interface CflObjectMapper {
    void insertObject(CflObject object);
    void updateObject(CflObject object);
    void deleteObject(CflObject object);
    CflObject selectObject(CflObject object);

    List<CflObject> selectAllObjects();
    List<CflObject> selectServiceObjects(String ServiceName);
    List<CflObject> selectTenantObjects(String ServiceName, String tenantId);
}
