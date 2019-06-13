package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.Code;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.CodeMapper;
import com.cfl.mapper.MappingMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class CacheService {
    @Autowired
    private NetworkService networkService;
    @Autowired
    private CflObjectMapper cflObjectMapper;
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private MappingMapper mappingMapper;

    //setup new object cache map
    private Map<String, Map<String, Map<String, CflObject>>> getObjects(List<CflObject> objectList){
        Map<String, Map<String, Map<String, CflObject>>> newObjectCacheMap = new HashMap<>();

        for(CflObject cflObject : objectList){
            //set service map
            String serviceName = cflObject.getServiceName();
            Map<String, Map<String, CflObject>> serviceNameMap = newObjectCacheMap.get(serviceName);
            if(serviceNameMap == null){
                serviceNameMap = new HashMap<>();
                newObjectCacheMap.put(serviceName, serviceNameMap);
            }
            //set tenant map
            String tenantId = cflObject.getTenantId();
            Map<String, CflObject> TenantIdMap = serviceNameMap.get(tenantId);
            if(TenantIdMap==null){
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId,TenantIdMap);
            }
            //set object map
            TenantIdMap.put(cflObject.getObjectId(), cflObject);
        }
        return newObjectCacheMap;
    }

    private Map<String, Map<String, Map<String, Code>>> getCodes(List<Code> codeList){
        Map<String, Map<String, Map<String, Code>>> newCodeCacheMap = new HashMap<>();

        for(Code code : codeList){
            String serviceName = code.getServiceName();
            Map<String, Map<String, Code>> serviceNameMap = newCodeCacheMap.get(serviceName);
            if(serviceNameMap == null){
                serviceNameMap = new HashMap<>();
                newCodeCacheMap.put(serviceName, serviceNameMap);
            }
            String tenantId = code.getTenantId();
            Map<String, Code> TenantIdMap = serviceNameMap.get(tenantId);
            if(TenantIdMap==null){
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId,TenantIdMap);
            }
            TenantIdMap.put(code.getCodeId(), code);
        }
        return newCodeCacheMap;
    }

    private Map<String, Map<String, Map<String, CflObject>>> addSubObjectsAndAuthorities(Map<String, Map<String, Map<String, CflObject>>> objectMap){
        Set<String> serviceKeySet = objectMap.keySet();
        Iterator<String> serviceKeyIterator = serviceKeySet.iterator();
        //into service map
        while(serviceKeyIterator.hasNext()){
            String serviceName = serviceKeyIterator.next();
            Map<String,Map<String, CflObject>> tenantIdMap = objectMap.get(serviceName);
            if(tenantIdMap != null){
                Set<String> tenantKeySet = tenantIdMap.keySet();
                Iterator<String> tenantKeyIterator = tenantKeySet.iterator();
                //into tenant map
                while (tenantKeyIterator.hasNext()){
                    String tenantId = tenantKeyIterator.next();
                    Map<String, CflObject> objectIdMap = tenantIdMap.get(tenantId);
                    if(objectIdMap != null){
                        Set<String> objectKeySet = objectIdMap.keySet();
                        Iterator<String> objectKeyIterator = objectKeySet.iterator();

                        Map<String, List<String>> objectIdSubObjectIdListMap = mappingMapper.selectObjectIdSubObjectIdListMap(serviceName, tenantId);
                        Map<String, List<Authority>> objectIdAuthoritiesMap = mappingMapper.selectObjectIdAuthoritiesMap(serviceName, tenantId);
                        //into object map
                        while(objectKeyIterator.hasNext()){
                            String objectId = objectKeyIterator.next();
                            CflObject object = objectIdMap.get(objectId);
                            if (object != null) {
                                //add subobjects
                                if(objectIdSubObjectIdListMap != null) {
                                    List<String> subObjectIdList = objectIdSubObjectIdListMap.get(objectId);
                                    Map<String, CflObject> subObjectMap = new HashMap<>();
                                    for (String subObjectId : subObjectIdList) {
                                        subObjectMap.put(subObjectId, objectMap.get(serviceName).get(tenantId).get(subObjectId));
                                    }
                                    object.setSubObjects(subObjectMap);
                                }
                                //add authorities
                                if(objectIdAuthoritiesMap != null){
                                    List<Authority> authorityList = objectIdAuthoritiesMap.get(objectId);
                                    object.setAuthorities(authorityList);
                                }
                            }
                        }
                    }
                }
            }
        }
        return objectMap;
    }

    public ApiResponse refreshTenantObjectCache(String serviceName, String tenantId){
        try {
            List<CflObject> objectList = cflObjectMapper.selectTenantObjects(serviceName, tenantId);
            Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjects(objectList);
            Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryCache);
            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse refreshServiceObjectCache(String serviceName){
        try {
            List<CflObject> objectList = cflObjectMapper.selectServiceObjects(serviceName);
            Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjects(objectList);
            Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryCache);
            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    public ApiResponse createObjectCache() {
        try {
            List<CflObject> objectList = cflObjectMapper.selectAllObjects();
            Map<String, Map<String, Map<String, CflObject>>> temporaryObjectCache = getObjects(objectList);
            Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryObjectCache);
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse refreshTenantCodeCache(String serviceName, String tenantId){
        try {
            List<Code> codeList = codeMapper.selectTenantCodes(serviceName, tenantId);
            Cache.codeCache = getCodes(codeList);
            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse refreshServiceCodeCache(String serviceName){
        try {
            List<Code> codeList = codeMapper.selectServiceCodes(serviceName);
            Cache.codeCache = getCodes(codeList);
            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    public ApiResponse createCodeCache() {
        try {
            List<Code> codeList = codeMapper.selectAllCodes();
            Cache.codeCache = getCodes(codeList);
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }


    public ApiResponse clearUserAuthorityCache(){
        try {
            synchronized (Cache.authorityUserCache) {
                Cache.authorityUserCache.clear();
            }
            synchronized (Cache.userAuthorityCache) {
                Cache.userAuthorityCache.clear();
            }
            return ApiResponseUtil.getSuccessApiResponse(new Object());
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse clearUserAuthorityServiceCache(String serviceName){
        try {
            synchronized (Cache.authorityUserCache) {
                if (Cache.authorityUserCache.get(serviceName) != null) {
                    Cache.authorityUserCache.get(serviceName).clear();
                }
            }
            synchronized (Cache.userAuthorityCache) {
                if (Cache.userAuthorityCache.get(serviceName) != null) {
                    Cache.userAuthorityCache.get(serviceName).clear();
                }
            }
            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(new Object());
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse clearUserAuthorityTenantCache(String serviceName, String tenantId) {
        try {
            if (tenantId == null) {
                tenantId = Constant.DEFAULT_TENANT_ID;
            }
            synchronized (Cache.authorityUserCache) {
                if (Cache.authorityUserCache.get(serviceName) != null) {
                    if (Cache.authorityUserCache.get(serviceName).get(tenantId) != null) {
                        Cache.authorityUserCache.get(serviceName).get(tenantId).clear();
                    }
                }
            }
            synchronized (Cache.userAuthorityCache) {
                if (Cache.userAuthorityCache.get(serviceName) != null) {
                    if (Cache.userAuthorityCache.get(serviceName).get(tenantId) != null) {
                        Cache.userAuthorityCache.get(serviceName).get(tenantId).clear();
                    }
                }
            }
            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(new Object());
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse allCacheInit(String serverIp){
        try {
            createCodeCache();
            createObjectCache();
            clearUserAuthorityCache();
            return ApiResponseUtil.getSuccessApiResponse(serverIp);
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
}
