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
    private HistoryService historyService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private NetworkService networkService;

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
        ApiResponse apiResponse;

        try {
            object.setServiceName(serviceName);
            object.setTenantId(tenantId);
            object.setObjectId(objectId);

            CflObject selectedObject = cflObjectMapper.selectObject(object);

            // 오브젝트 중복 생성의 경우
            if (selectedObject != null) {
                apiResponse = ApiResponseUtil.getDuplicateCreationApiResponse();
            } else {
                cflObjectMapper.insertObject(object);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "object"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            }
        } catch (Exception e) {
            log.error("createObject fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), object, apiResponse);
        return apiResponse;
    }

    public ApiResponse modifyObject(String serviceName, String tenantId, String objectId, CflObject object) {
        ApiResponse apiResponse;

        try {
            object.setServiceName(serviceName);
            object.setTenantId(tenantId);
            object.setObjectId(objectId);

            CflObject selectedObject = cflObjectMapper.selectObject(object);

            // 존재하지 않는 오브젝트을 수정하려는 경우
            if (selectedObject == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                cflObjectMapper.updateObject(object);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "object"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            }
        } catch (Exception e) {
            log.error("modifyObject fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), object, apiResponse);
        return apiResponse;
    }

    public ApiResponse removeObject(String serviceName, String tenantId, String objectId) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            CflObject selectedObject = cflObjectMapper.selectObject(object);

            // 존재하지 않는 오브젝트를 삭제하려는 경우
            if (selectedObject == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                // 오브젝트 삭제 전 매핑 정보부터 우선 삭제 후 오브젝트 삭제 진행
                mappingService.removeObjectMapping(object);
                cflObjectMapper.deleteObject(object);

                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "object"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(object);
            }
        } catch (Exception e) {
            log.error("removeObject fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), object, apiResponse);
        return apiResponse;
    }

    public ApiResponse getObject(String serviceName, String tenantId, String objectId) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            CflObject objectFromCache = getObjectFromCache(object);

            // 캐시에 오브젝트가 없는 경우
            if (objectFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(objectFromCache);
            }
        } catch (Exception e) {
            log.error("getObject fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    public ApiResponse createObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            List<Authority> duplicatedAuthorityList = new ArrayList<>();

            for (Authority requestAuthority : requestAuthorities) {
                requestAuthority.setServiceName(serviceName);
                requestAuthority.setTenantId(object.getTenantId());
                requestAuthority = authorityService.checkExistAuthorityAndCreateAuthority(requestAuthority);

                // 이미 매핑이 되어있는 경우 중복권한리스트에 추가한다.
                if (mappingService.isExistObjectAuthorityMapping(objectId, requestAuthority)) {
                    duplicatedAuthorityList.add(requestAuthority);
                } else {
                    mappingService.createObjectAuthorityMappting(objectId, requestAuthority);
                }
            }

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "object"));

            if (duplicatedAuthorityList.size() == 0) {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
            } else {
                apiResponse = ApiResponseUtil.getDuplicateMappingApiResponse(duplicatedAuthorityList);
            }
        } catch (Exception e) {
            log.error("createObjectAuthoritiesMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, apiResponse);
        return apiResponse;
    }

    public ApiResponse removeObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            for (Authority requestAuthority : requestAuthorities) {
                requestAuthority.setServiceName(serviceName);
                requestAuthority.setTenantId(object.getTenantId());
                mappingService.removeObjectAuthorityMapping(objectId, requestAuthority);
            }

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "object"));
            apiResponse = ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
        } catch (Exception e) {
            log.error("removeObjectAuthoritiesMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, apiResponse);
        return apiResponse;
    }

    public ApiResponse getObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            List<Authority> authorityList = getObjectToAuthoritiesFromCache(object);

            // 캐시에 오브젝트의 권한리스트가 없는 경우
            if (authorityList == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authorityList);
            }
        } catch (Exception e) {
            log.error("getObjectAuthoritiesMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    public ApiResponse getTenantObjectMap(String serviceName, String tenantId) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId);

        try {
            Map<String, CflObject> tenantObjectMap = getTenantObjectMapFromCache(object);

            // 테넌트 맵이 없는 경우
            if (tenantObjectMap == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(tenantObjectMap);
            }
        } catch (Exception e) {
            log.error("getTenantObjectList fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    private Map<String, CflObject> getTenantObjectMapFromCache(CflObject object) {
        // 캐시에서 찾는 맵이 없는 경우 null 반환
        Map<String, Map<String, CflObject>> serviceMapFromCache = Cache.objectAuthorityCache.get(object.getServiceName());
        if (serviceMapFromCache == null) {
            return null;
        }

        return serviceMapFromCache.get(object.getTenantId());
    }

    private CflObject getObjectFromCache(CflObject object) {
        // 캐시에서 찾는 오브젝트이 없는 경우 null 반환
        Map<String, CflObject> tenantMapFromCache = getTenantObjectMapFromCache(object);
        if (tenantMapFromCache == null) {
            return null;
        }

        return tenantMapFromCache.get(object.getObjectId());
    }

    private List<Authority> getObjectToAuthoritiesFromCache(CflObject object) {
        CflObject objectFromCache = getObjectFromCache(object);
        if (objectFromCache == null) {
            return null;
        }

        return objectFromCache.getAuthorities();
    }
}
