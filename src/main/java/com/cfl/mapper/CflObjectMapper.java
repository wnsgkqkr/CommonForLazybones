package com.cfl.mapper;

import com.cfl.domain.CflObject;
import org.apache.ibatis.annotations.Mapper;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@Mapper
public interface CflObjectMapper {
    void insertCflObject(CflObject object);
    void updateCflObject(CflObject object);
    void deleteCflObject(CflObject object);
    CflObject selectCflObject(CflObject object);

    List<CflObject> selectAllObjects();
    List<CflObject> selectServiceObjects(String ServiceName);
    List<CflObject> selectTenantObjects(String ServiceName, String tenantId);

    Map<String, List<String>> selectObjectIdSubObjectIdListMap(String serviceName, String tenantId);
}
