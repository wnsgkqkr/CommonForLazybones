package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.MappingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ObjectService implements CflService<CflObject>{
    @Autowired
    private CflObjectMapper cflObjectMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private CommonService commonService;

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
    //object insert / update / delete Database and refresh cache
    public CflObject createData(ApiRequest request){
        CflObject object = setObject(request);
        cflObjectMapper.insertObject(object);
        refreshCache(object.getServiceName(), object.getTenantId());
        return object;
    }
    public CflObject modifyData(ApiRequest request){
        CflObject object = setObject(request);
        cflObjectMapper.updateObject(object);
        refreshCache(object.getServiceName(), object.getTenantId());
        return object;
    }
    public CflObject removeData(ApiRequest request){
        CflObject object = setObject(request);
        cflObjectMapper.deleteObject(object);
        refreshCache(object.getServiceName(), object.getTenantId());
        return object;
    }
    //get Object from cache
    public CflObject getData(ApiRequest request)
    {
        CflObject object = setObject(request);
        CflObject mapObject = Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).get(object.getObjectId());
        if(mapObject == null){
            object = cflObjectMapper.selectObject(object);
            Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).put(object.getObjectId(),object);
            return object;
        }
        return mapObject;
    }

    //mapping information insert / delete Database and refresh cache
    public ApiRequest createObjectAuthority(ApiRequest request){
        CflObject object = setObject(request);
        Authority authority = commonService.setAuthority(request);
        mappingMapper.insertObjectAuthority(object, authority);
        refreshCache(object.getServiceName(), object.getTenantId());
        return request;
    }
    public ApiRequest removeObjectAuthority(ApiRequest request){
        CflObject object = setObject(request);
        Authority authority = commonService.setAuthority(request);
        mappingMapper.deleteObjectAuthority(object, authority);
        refreshCache(object.getServiceName(), object.getTenantId());
        return request;
    }
    //get AuthorityIds in Object from cache
    public List<String> getObjectAuthorityIds(ApiRequest request){
        CflObject object = setObject(request);
        List<String> authorityIdList = Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).
                get(object.getObjectId()).getAuthorityIds();
        if(authorityIdList == null){
            authorityIdList = mappingMapper.selectObjectAuthorityIds(object);
            Cache.objectAuthorityCache.get(object.getServiceName()).get(object.getTenantId()).
                    get(object.getObjectId()).setAuthorityIds(authorityIdList);
        }
        return authorityIdList;
    }

    //VO request to CflObject Object
    public CflObject setObject(ApiRequest requestObject){
        CflObject object = requestObject.getObject();
        object.setServiceName(requestObject.getServiceName());
        object.setTenantId(requestObject.getTenantId());

        return object;
    }
}
