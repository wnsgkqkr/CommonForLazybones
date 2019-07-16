package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.*;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ObjectService {
    @Autowired
    private CflObjectMapper cflObjectMapper;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private MappingService mappingService;

    public List<CflObject> getAllObjects() {
        return cflObjectMapper.selectAllObjects();
    }

    public List<CflObject> getServiceObjects(String serviceName) {
        return cflObjectMapper.selectServiceObjects(serviceName);
    }

    public List<CflObject> getTenantObjects(String serviceName, String tenantId) {
        return cflObjectMapper.selectTenantObjects(serviceName, tenantId);
    }

    public ApiResponse createObject(String serviceName, String tenantId, String objectId, CflObject object) {
        try {
            object.setServiceName(serviceName);
            object.setTenantId(tenantId);
            object.setObjectId(objectId);

            cflObjectMapper.insertObject(object);
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            historyService.createHistory(serviceName, object.getTenantId(), object, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            log.error("createObject fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyObject(String serviceName, String tenantId, String objectId, CflObject object) {
        try {
            object.setServiceName(serviceName);
            object.setTenantId(tenantId);
            object.setObjectId(objectId);

            cflObjectMapper.updateObject(object);
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            historyService.createHistory(serviceName, object.getTenantId(), object, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            log.error("modifyObject fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeObject(String serviceName, String tenantId, String objectId) {
        try {
            CflObject object = new CflObject(serviceName, tenantId, objectId);

            // 오브젝트 삭제 전 매핑 정보부터 우선 삭제 후 오브젝트 삭제 진행
            mappingService.removeObjectMapping(object);
            cflObjectMapper.deleteObject(object);

            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            historyService.createHistory(serviceName, object.getTenantId(), object, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            log.error("removeObject fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getObject(String serviceName, String tenantId, String objectId) {
        try {
            CflObject object = new CflObject(serviceName, tenantId, objectId);

            CflObject objectFromCache = Cache.objectAuthorityCache.get(serviceName).get(object.getTenantId()).get(objectId);

            ApiResponse apiResponse;

            // 캐시에 오브젝트가 없는 경우
            if (objectFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(objectFromCache);
            }
            historyService.createHistory(serviceName, objectFromCache.getTenantId(), objectFromCache, apiResponse.getHeader().getResultMessage());
            return apiResponse;
        } catch (Exception e) {
            log.error("getObject fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse createObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        try {
            CflObject object = new CflObject(serviceName, tenantId, objectId);
            List<Authority> duplicatedAuthorityList = new ArrayList<>();

            for (Authority requestAuthority : requestAuthorities) {
                requestAuthority.setServiceName(serviceName);
                requestAuthority.setTenantId(object.getTenantId());
                requestAuthority = authorityService.checkExistAndCreateAuthority(requestAuthority);

                if (mappingService.isExistObjectAuthorityMapping(objectId, requestAuthority)) {
                    duplicatedAuthorityList.add(requestAuthority);
                } else {
                    mappingService.createObjectAuthorityMappting(objectId, requestAuthority);
                }
            }

            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());

            ApiResponse apiResponse;

            if (duplicatedAuthorityList.size() == 0) {
                apiResponse =  ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
            } else {
                apiResponse = ApiResponseUtil.getDuplicateApiResponse(duplicatedAuthorityList);
            }

            historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, apiResponse.getHeader().getResultMessage());
            return apiResponse;
        } catch (Exception e) {
            log.error("createObjectAuthoritiesMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        try {
            CflObject object = new CflObject(serviceName, tenantId, objectId);
            for (Authority requestAuthority : requestAuthorities) {
                requestAuthority.setServiceName(serviceName);
                requestAuthority.setTenantId(object.getTenantId());
                mappingService.removeObjectAuthorityMapping(objectId, requestAuthority);
            }
            cacheService.refreshTenantObjectCache(serviceName, object.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
            historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            log.error("removeObjectAuthoritiesMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId) {
        try {
            CflObject object = new CflObject(serviceName, tenantId, objectId);

            List<Authority> authorityList = Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).get(object.getObjectId()).getAuthorities();

            ApiResponse apiResponse;

            // 캐시에 오브젝트 권한리스트가 없는 경우
            if (authorityList == null) {
                apiResponse = ApiResponseUtil.getMissingValueResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authorityList);
            }

            historyService.createHistory(serviceName, object.getTenantId(), authorityList, apiResponse.getHeader().getResultMessage());
            return apiResponse;
        } catch (Exception e) {
            log.error("getObjectAuthoritiesMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getTenantObjectList(String serviceName, String tenantId) {
        try {
            CflObject cflObject = new CflObject(serviceName, tenantId);

            Map<String, CflObject> objectMap = getObjectMapFromCache(cflObject);
            if (objectMap != null) {
                List<CflObject> tenantObjects = new ArrayList<>(objectMap.values());
                return ApiResponseUtil.getSuccessApiResponse(tenantObjects);
            } else {
                List<CflObject> tenantObjects = cflObjectMapper.selectTenantObjects(serviceName, tenantId);
                return ApiResponseUtil.getSuccessApiResponse(tenantObjects);
            }
        } catch (Exception e) {
            log.error("getTenantObjectList fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, CflObject> getObjectMapFromCache(CflObject cflObject) {
        Map<String, Map<String, CflObject>> serviceNameMap = Cache.objectAuthorityCache.get(cflObject.getServiceName());
        return serviceNameMap.get(cflObject.getTenantId());
    }
}
