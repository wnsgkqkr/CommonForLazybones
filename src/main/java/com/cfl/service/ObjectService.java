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

    /***
     * 오브젝트와 권한 리스트를 매핑하는 메서드
     * 존재하지 않는 오브젝트나 권한을 요청으로 주는 경우 Missing value 결과를 반환한다.
     * 이미 존재하는 오브젝트-권한 매핑을 요청하는 경우 중복리스트에 넣어 Duplicate mapping 결과에 넣어 반환한다.
     * (나머지 존재하지 않던 오브젝트-권한 매핑은 정상적으로 매핑한다.)
     */
    public ApiResponse createObjectAuthoritiesMapping(String serviceName, String tenantId, String objectId, List<Authority> requestAuthorities) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            CflObject objectFromCache = getObjectFromCache(object);

            // 오브젝트가 없는 경우
            if (objectFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                // 존재하지 않는 권한이 있는 경우
                if (hasNonExistentAuithority(object, requestAuthorities)) {
                    apiResponse = ApiResponseUtil.getMissingValueApiResponse();
                } else {
                    List<Authority> duplicatedAuthorityList = new ArrayList<>();

                    for (Authority requestAuthority : requestAuthorities) {
                        // 이미 매핑이 되어있는 경우 중복권한리스트에 추가한다.
                        if (mappingService.isExistObjectAuthorityMapping(objectId, requestAuthority)) {
                            duplicatedAuthorityList.add(requestAuthority);
                        } else {
                            mappingService.createObjectAuthorityMappting(objectId, requestAuthority);
                        }
                    }

                    networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId, "object"));

                    // 중복된 매핑이 있는 경우 Duplicate mapping이 없는 경우 Success로 결과를 세팅한다.
                    if (duplicatedAuthorityList.size() == 0) {
                        apiResponse = ApiResponseUtil.getSuccessApiResponse(requestAuthorities);
                    } else {
                        apiResponse = ApiResponseUtil.getDuplicateMappingApiResponse(duplicatedAuthorityList);
                    }
                }
            }
        } catch (Exception e) {
            log.error("createObjectAuthoritiesMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), requestAuthorities, apiResponse);
        return apiResponse;
    }

    private boolean hasNonExistentAuithority(CflObject object, List<Authority> requestAuthorities) {
        boolean hasNonExistentAuithority = false;

        // 존재하지 않는 권한이 있는지 확인
        for (Authority requestAuthority : requestAuthorities) {
            requestAuthority.setServiceName(object.getServiceName());
            requestAuthority.setTenantId(object.getTenantId());
            requestAuthority = authorityService.getAuthorityFromCache(requestAuthority);

            if (requestAuthority == null) {
                hasNonExistentAuithority = true;
                break;
            }
        }

        return hasNonExistentAuithority;
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

            // 캐시에 오브젝트에 권한리스트가 없는 경우
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

    /***
     * 오브젝트와 권한 리스트를 매핑하는 메서드
     * 존재하지 않는 오브젝트나 서브 오브젝트를 요청으로 주는 경우 Missing value 결과를 반환한다.
     * 연결하면 안되는 오브젝트-서브 오브젝트가 존재시 Sub Object Mapping Error 결과를 반환한다.
     * 이미 존재하는 오브젝트-서브 오브젝트 매핑을 요청하는 경우 중복리스트에 넣어 Duplicate mapping 결과에 넣어 반환한다.
     * (나머지 존재하지 않던 오브젝트-서브 오브젝트 매핑은 정상적으로 매핑한다.)
     */
    public ApiResponse createObjectSubObjectsMapping(String serviceName, String tenantId, String objectId, List<CflObject> requestSubObjects) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            CflObject objectFromCache = getObjectFromCache(object);

            // 오브젝트가 없는 경우
            if (objectFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                // 요청된 서브 오브젝트 중 존재하지 않는 오브젝트가 있는 경우
                if (hasNonExistentObject(object, requestSubObjects)) {
                    apiResponse = ApiResponseUtil.getMissingValueApiResponse();
                } else {
                    // 연결하면 안되는 오브젝트가 존재하는 경우
                    if (hasSubObjectMappingError(object, requestSubObjects)) {
                        apiResponse = ApiResponseUtil.getSubObjectMappingErrorApiResponse();
                    } else {
                        List<CflObject> duplicatedObjectList = new ArrayList<>();

                        for (CflObject requestSubObject : requestSubObjects) {
                            // 이미 매핑이 되어있는 경우 중복서브오브젝트리스트에 추가한다.
                            if (mappingService.isExistObjectSubObjectMapping(objectId, requestSubObject)) {
                                duplicatedObjectList.add(requestSubObject);
                            } else {
                                mappingService.createObjectSubObjectMapping(objectId, requestSubObject);
                            }
                        }

                        networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId, "object"));

                        // 중복된 매핑이 있는 경우 Duplicate mapping이 없는 경우 Success로 결과를 세팅한다.
                        if (duplicatedObjectList.size() == 0) {
                            apiResponse = ApiResponseUtil.getSuccessApiResponse(requestSubObjects);
                        } else {
                            apiResponse = ApiResponseUtil.getDuplicateMappingApiResponse(duplicatedObjectList);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("createObjectSubObjectsMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), requestSubObjects, apiResponse);
        return apiResponse;
    }

    private boolean hasNonExistentObject(CflObject object, List<CflObject> requestSubObjects) {
        boolean hasNonExistentObject = false;

        // 서브 오브젝트 요청온 것 중 존재하지 않는 오브젝트는 없는지 확인
        for (CflObject requestSubObject : requestSubObjects) {
            requestSubObject.setServiceName(object.getServiceName());
            requestSubObject.setTenantId(object.getTenantId());
            requestSubObject = getObjectFromCache(requestSubObject);

            if (requestSubObject == null) {
                hasNonExistentObject = true;
                break;
            }
        }

        return hasNonExistentObject;
    }

    /***
     * 오브젝트-서브 오브젝트를 연결하면 안 되는 매핑이 있는지 확인하는 메서드
     * 오브젝트는 2계층(오브젝트-서브 오브젝트)으로 구성되도록 설정한다. (2계층을 넘어가게 하는 매핑이 요청 온 경우 에러를 반환하도록 한다.)
     * 부모 오브젝트로 요청 온 오브젝트가 다른 곳에서 서브 오브젝트로 사용되었다면 true 반환
     * 브 오브젝트로 요청 온 오브젝트가 다른 곳에서 부모 오브젝트로 사용되었다면 true 반환
     */
    private boolean hasSubObjectMappingError(CflObject object, List<CflObject> requestSubObjects) {
        boolean hasSubObjectMappingError = false;

        // 요청된 부모 오브젝트가 자식으로 사용된 게 있는지 확인 (2계층 구조 이므로)
        List<String> tenantSubObjectIdList = mappingService.getTenantSubObjectIdList(object.getServiceName(), object.getTenantId());
        for (String subObjectId : tenantSubObjectIdList) {
            if (object.getObjectId().equals(subObjectId)) {
                hasSubObjectMappingError = true;
                break;
            }
        }

        // 요청된 자식 오브젝트들이 부모로 사용된 게 있는지 확인 (2계층 구조 이므로)
        List<String> tenantParentObjectIdList = mappingService.getTenantParentObjectIdList(object.getServiceName(), object.getTenantId());
        for (CflObject requestSubObject : requestSubObjects) {
            for(String parentObjectId : tenantParentObjectIdList) {
                if (requestSubObject.getObjectId().equals(parentObjectId)) {
                    hasSubObjectMappingError = true;
                    break;
                }
            }
        }

        return hasSubObjectMappingError;
    }

    public ApiResponse removeObjectSubObjectsMapping(String serviceName, String tenantId, String objectId, List<CflObject> requestSubObjects) {
        ApiResponse apiResponse;
        CflObject object = new CflObject(serviceName, tenantId, objectId);

        try {
            for (CflObject requestSubObject : requestSubObjects) {
                requestSubObject.setServiceName(serviceName);
                requestSubObject.setTenantId(object.getTenantId());
                mappingService.removeObjectSubObjectMapping(objectId, requestSubObject);
            }

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "object"));
            apiResponse = ApiResponseUtil.getSuccessApiResponse(requestSubObjects);
        } catch (Exception e) {
            log.error("removeObjectSubObjectsMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, object.getTenantId(), requestSubObjects, apiResponse);
        return apiResponse;
    }
}
