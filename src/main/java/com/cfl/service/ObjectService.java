package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.MappingMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ObjectService {
    @Autowired
    private CflObjectMapper cflObjectMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

    public ApiResponse createObject(String serviceName, String tenantId, String objectId, CflObject object) {
        try {
            object = setObject(serviceName, tenantId, objectId, object);
            cflObjectMapper.insertObject(object);
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            historyService.createHistory(serviceName, object.getTenantId(), object, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyObject(String serviceName, String tenantId, String objectId, CflObject object) {
        try {
            object = setObject(serviceName, tenantId, objectId, object);
            cflObjectMapper.updateObject(serviceName, object.getTenantId(), objectId, object);
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            historyService.createHistory(serviceName, object.getTenantId(), object, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeObject(String serviceName, String tenantId, String objectId) {
        try {
            CflObject object = setObject(serviceName, tenantId, objectId, new CflObject());
            cflObjectMapper.deleteObject(object);
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            historyService.createHistory(serviceName, object.getTenantId(), object, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    //get Object from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getObject(String serviceName, String tenantId, String objectId) {
        try {
            CflObject object = setObject(serviceName, tenantId, objectId, new CflObject());
            CflObject mapObject = Cache.objectAuthorityCache.get(serviceName).get(object.getTenantId()).get(objectId);
            if(mapObject == null){
                object = cflObjectMapper.selectObject(object);
                Cache.objectAuthorityCache.get(serviceName).get(object.getTenantId()).put(objectId, object);
                mapObject = object;
            }
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(mapObject);
            historyService.createHistory(serviceName, mapObject.getTenantId(), mapObject, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse createObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        try {
            CflObject object = setObject(serviceName, tenantId, objectId, new CflObject());
            for(Authority requestAuthority : requestAuthorities) {
                requestAuthority.setServiceName(serviceName);
                requestAuthority.setTenantId(object.getTenantId());
                requestAuthority = authorityService.checkExistAndCreateAuthority(requestAuthority);
                mappingMapper.insertObjectAuthority(objectId, requestAuthority);
            }
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
            historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        try {
            CflObject object = setObject(serviceName, tenantId, objectId, new CflObject());
            for(Authority requestAuthority : requestAuthorities) {
                requestAuthority.setServiceName(serviceName);
                requestAuthority.setTenantId(object.getTenantId());
                mappingMapper.deleteObjectAuthority(objectId, requestAuthority);
            }
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
            historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    //get AuthorityList in Object from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId) {
        try {
            CflObject object = setObject(serviceName, tenantId, objectId, new CflObject());
            List<Authority> authorityList = Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).get(object.getObjectId()).getAuthorities();
            if (authorityList == null) {
                authorityList = mappingMapper.selectObjectAuthorities(object);
                Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).get(object.getObjectId()).setAuthorities(authorityList);
            }
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authorityList);
            historyService.createHistory(serviceName, object.getTenantId(), authorityList, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private CflObject setObject(String serviceName, String tenantId, String objectId, CflObject object) {
        object.setServiceName(serviceName);
        if(tenantId == null) {
            object.setTenantId(Constant.DEFAULT_TENANT_ID);
        } else {
            object.setTenantId(tenantId);
        }
        object.setObjectId(objectId);

        return object;
    }
}
