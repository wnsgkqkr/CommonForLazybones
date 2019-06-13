package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.CflObject;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.MappingMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;

public class CacheService {
    @Autowired
    private CflObjectMapper cflObjectMapper;
    @Autowired
    private MappingMapper mappingMapper;

    //setup new cache map
    private Map<String, Map<String, Map<String, CflObject>>> getObjects(List<CflObject> objectList){
        Map<String, Map<String, Map<String, CflObject>>> newCacheMap = new HashMap<>();

        for(CflObject cflObject : objectList){
            //set service map
            String serviceName = cflObject.getServiceName();
            Map<String, Map<String, CflObject>> serviceNameMap = newCacheMap.get(serviceName);
            if(serviceNameMap == null){
                serviceNameMap = new HashMap<>();
                newCacheMap.put(serviceName, serviceNameMap);
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
        return newCacheMap;
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
                        Map<String, List<String>> objectIdAuthorityIdListMap = mappingMapper.selectObjectIdAuthorityIdListMap(serviceName, tenantId);
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
                                if(objectIdAuthorityIdListMap != null){
                                    List<String> authorityIdList = objectIdAuthorityIdListMap.get(objectId);
                                    object.setAuthorityIds(authorityIdList);
                                }
                            }
                        }
                    }
                }
            }
        }
        return objectMap;
    }

    public Map<String, Map<String, Map<String, CflObject>>> refreshCache(String serviceName, String tenantId){
        List<CflObject> objectList = cflObjectMapper.selectTenantObjects(serviceName,tenantId);
        Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjects(objectList);
        Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryCache);
        return Cache.objectAuthorityCache;
    }
    public Map<String, Map<String, Map<String, CflObject>>> refreshCache(String serviceName){
        List<CflObject> objectList = cflObjectMapper.selectServiceObjects(serviceName);
        Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjects(objectList);
        Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryCache);
        return Cache.objectAuthorityCache;
    }

    @PostConstruct
    public Map<String, Map<String, Map<String, CflObject>>> createCache(){
        List<CflObject> objectList = cflObjectMapper.selectAllObjects();
        Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjects(objectList);
        Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryCache);
        return Cache.objectAuthorityCache;
    }


    public void clearUserAuthorityCache(){
        synchronized (Cache.authorityUserCache) {
            Cache.authorityUserCache.clear();
        }
        synchronized (Cache.userAuthorityCache){
            Cache.userAuthorityCache.clear();
        }
    }
    public void clearUserAuthorityServiceCache(String serviceName){
        synchronized (Cache.authorityUserCache) {
            if(Cache.authorityUserCache.get(serviceName)!=null) {
                Cache.authorityUserCache.get(serviceName).clear();
            }
        }
        synchronized (Cache.userAuthorityCache){
            if(Cache.userAuthorityCache.get(serviceName)!=null) {
                Cache.userAuthorityCache.get(serviceName).clear();
            }
        }
    }
    public void clearUserAuthorityTenantCache(String serviceName,String tenantId){
        synchronized (Cache.authorityUserCache) {
            if(Cache.authorityUserCache.get(serviceName)!=null){
                if(Cache.authorityUserCache.get(serviceName).get(tenantId)!=null){
                    Cache.authorityUserCache.get(serviceName).get(tenantId).clear();
                }
            }
        }
        synchronized (Cache.userAuthorityCache){
            if(Cache.userAuthorityCache.get(serviceName)!=null){
                if(Cache.userAuthorityCache.get(serviceName).get(tenantId)!=null){
                    Cache.userAuthorityCache.get(serviceName).get(tenantId).clear();
                }
            }
        }
    }
}
