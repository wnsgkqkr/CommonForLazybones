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
    @Autowired
    private CacheService cacheService;

    //object insert / update / delete Database and refresh cache
    public CflObject createData(ApiRequest requestObject){
        CflObject object = setObject(requestObject);
        cflObjectMapper.insertObject(object);
        cacheService.refreshCache(object.getServiceName(), object.getTenantId());
        return object;
    }
    public CflObject modifyData(ApiRequest requestObject){
        CflObject object = setObject(requestObject);
        cflObjectMapper.updateObject(object);
        cacheService.refreshCache(object.getServiceName(), object.getTenantId());
        return object;
    }
    public CflObject removeData(ApiRequest requestObject){
        CflObject object = setObject(requestObject);
        cflObjectMapper.deleteObject(object);
        cacheService.refreshCache(object.getServiceName(), object.getTenantId());
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
    public ApiRequest createObjectAuthority(ApiRequest requestObject){
        CflObject object = setObject(requestObject);
        Authority authority = commonService.setAuthority(requestObject);
        mappingMapper.insertObjectAuthority(object, authority);
        cacheService.refreshCache(object.getServiceName(), object.getTenantId());
        return requestObject;
    }
    public ApiRequest removeObjectAuthority(ApiRequest requestObject){
        CflObject object = setObject(requestObject);
        Authority authority = commonService.setAuthority(requestObject);
        mappingMapper.deleteObjectAuthority(object, authority);
        cacheService.refreshCache(object.getServiceName(), object.getTenantId());
        return requestObject;
    }
    //get AuthorityIds in Object from cache
    public List<String> getObjectAuthorityIds(ApiRequest requestObject){
        CflObject object = setObject(requestObject);
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
    private CflObject setObject(ApiRequest requestObject){
        CflObject object = requestObject.getObject();
        object.setServiceName(requestObject.getServiceName());
        object.setTenantId(requestObject.getTenantId());

        return object;
    }
}
