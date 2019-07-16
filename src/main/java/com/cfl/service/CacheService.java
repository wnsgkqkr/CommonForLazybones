package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.*;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.CodeMapper;
import com.cfl.mapper.MappingMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
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
    @Autowired
    private AuthorityMapper authorityMapper;


    public ApiResponse allCacheInit(String serverIp){
        try {
            createObjectCache();
            createAuthorityCache();
            createCodeCache();
            clearUserCache();
            return ApiResponseUtil.getSuccessApiResponse(serverIp);
        } catch (Exception e) {
            log.error("allCacheInit fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    public ApiResponse createObjectCache() {
        try {
            List<CflObject> objectList = cflObjectMapper.selectAllObjects();
            Map<String, Map<String, Map<String, CflObject>>> temporaryObjectCache = getObjectMap(objectList);
            Cache.objectAuthorityCache = addSubObjectsAndAuthorities(temporaryObjectCache);
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            log.error("createObjectCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Map<String, Map<String, CflObject>>> getObjectMap(List<CflObject> objectList){
        Map<String, Map<String, Map<String, CflObject>>> newObjectCacheMap = new HashMap<>();

        for (CflObject cflObject : objectList) {
            // service map이 없는 경우 생성
            String serviceName = cflObject.getServiceName();
            Map<String, Map<String, CflObject>> serviceNameMap = newObjectCacheMap.get(serviceName);
            if (serviceNameMap == null) {
                serviceNameMap = new HashMap<>();
                newObjectCacheMap.put(serviceName, serviceNameMap);
            }

            // tenant map이 없는 경우 생성
            String tenantId = cflObject.getTenantId();
            Map<String, CflObject> TenantIdMap = serviceNameMap.get(tenantId);
            if (TenantIdMap==null) {
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId,TenantIdMap);
            }

            // 맵에 오브젝트 저장
            TenantIdMap.put(cflObject.getObjectId(), cflObject);
        }
        return newObjectCacheMap;
    }

    private Map<String, Map<String, Map<String, CflObject>>> addSubObjectsAndAuthorities(Map<String, Map<String, Map<String, CflObject>>> objectMap) {

        // 서비스 이름 keySet 및 iterator 세팅
        Set<String> serviceNameKeySet = objectMap.keySet();
        Iterator<String> serviceNameKeyIterator = serviceNameKeySet.iterator();
        // 서비스 이름으로 반복문을 돌며 서브 오브젝트와 매핑된 권한 리스트를 오브젝트에 넣어준다.
        while (serviceNameKeyIterator.hasNext()) {

            String serviceName = serviceNameKeyIterator.next();
            Map<String,Map<String, CflObject>> tenantIdMap = objectMap.get(serviceName);
            if (tenantIdMap != null) {
                // 테넌트 아이디 keySet 및 iterator 세팅
                Set<String> tenantIdKeySet = tenantIdMap.keySet();
                Iterator<String> tenantIdKeyIterator = tenantIdKeySet.iterator();
                // 테넌트 아이디로 반복문을 돌며 서브 오브젝트와 매핑된 권한 리스트를 오브젝트에 넣어준다.
                while (tenantIdKeyIterator.hasNext()) {

                    String tenantId = tenantIdKeyIterator.next();
                    Map<String, CflObject> objectIdMap = tenantIdMap.get(tenantId);
                    if (objectIdMap != null) {
                        // 오브젝트 아이디 keySet 및 iterator 세팅
                        Set<String> objectKeySet = objectIdMap.keySet();
                        Iterator<String> objectKeyIterator = objectKeySet.iterator();

                        // 서브 오브젝트 맵핑 정보 및 권한 매핑 정보 세팅
                        List<Map<String, String>> objectIdAndSubObjectIdMapList = mappingMapper.selectObjectIdAndSubObjectIdMapList(serviceName, tenantId);
                        List<Map<String, Object>> objectIdAndAuthorityMapList = mappingMapper.selectObjectIdAndAuthorityMapList(serviceName, tenantId);

                        // 오브젝트 아이디로 반복문을 돌며 서브 오브젝트와 매핑된 권한 리스트를 오브젝트에 넣어준다.
                        while (objectKeyIterator.hasNext()) {
                            String objectId = objectKeyIterator.next();
                            CflObject object = objectIdMap.get(objectId);
                            if (object != null) {
                                // 서브 오브젝트 세팅
                                if (objectIdAndSubObjectIdMapList != null) {
                                    List<String> subObjectIdList = getSubObjectIdList(objectId, objectIdAndSubObjectIdMapList);
                                    Map<String, CflObject> subObjectMap = new HashMap<>();

                                    // 서브 오브젝트의 경우 기존에 맵에 있던 인스턴스를 넣는다.
                                    for (String subObjectId : subObjectIdList) {
                                        subObjectMap.put(subObjectId, objectMap.get(serviceName).get(tenantId).get(subObjectId));
                                    }
                                    object.setSubObjects(subObjectMap);
                                }

                                // 권한 매핑 세팅
                                if (objectIdAndAuthorityMapList != null){
                                    List<Authority> authorityList = getAuthorityList(objectId, objectIdAndAuthorityMapList);
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

    private List<String> getSubObjectIdList(String objectId, List<Map<String, String>> objectIdAndSubObjectIdMapList) {

        List<String > subObjectIdList = new ArrayList<>();

        for (Map<String, String> map : objectIdAndSubObjectIdMapList) {
            if (objectId.equals(map.get("objectId"))) {
                subObjectIdList.add(map.get("subObjectId"));
            }
        }

        return subObjectIdList;
    }

    private List<Authority> getAuthorityList(String objectId, List<Map<String, Object>> objectIdAndAuthorityMapList) {

        List<Authority> authorityList = new ArrayList<>();

        for (Map<String, Object> map : objectIdAndAuthorityMapList) {
            if (objectId.equals(map.get("objectId"))) {
                authorityList.add(getConvertingAuthority(map));
            }
        }

        return authorityList;
    }

    private Authority getConvertingAuthority(Map<String, Object> map) {
        Authority authority = new Authority();

        authority.setAuthoritySequence((long)map.get("authoritySequence"));
        authority.setAuthorityId((String) map.get("authorityId"));
        authority.setAuthorityName((String) map.get("authorityName"));
        authority.setAuthorityType((String) map.get("authorityType"));
        authority.setTenantId((String) map.get("tenantId"));
        authority.setServiceName((String) map.get("serviceName"));

        return authority;
    }

    public ApiResponse refreshTenantObjectCache(String serviceName, String tenantId) {
        try {
            List<CflObject> objectList = cflObjectMapper.selectTenantObjects(serviceName, tenantId);
            Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjectMap(objectList);
            Map<String, Map<String, Map<String, CflObject>>> toBeChangedTenantMap = addSubObjectsAndAuthorities(temporaryCache);

            // 캐시 안에 기존 테넌트 맵 지우고 새로운 테넌트 맵 저장
            Map<String, Map<String, CflObject>> cacheServiceMap = Cache.objectAuthorityCache.get(serviceName);
            cacheServiceMap.remove(tenantId);
            cacheServiceMap.put(tenantId, toBeChangedTenantMap.get(serviceName).get(tenantId));

//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            log.error("refreshTenantObjectCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse refreshServiceObjectCache(String serviceName) {
        try {
            List<CflObject> objectList = cflObjectMapper.selectServiceObjects(serviceName);
            Map<String, Map<String, Map<String, CflObject>>> temporaryCache = getObjectMap(objectList);
            Map<String, Map<String, Map<String, CflObject>>> toBeChangedServiceMap = addSubObjectsAndAuthorities(temporaryCache);

            // 캐시 안에 기존 서비스 맵 지우고 새로운 서비스 맵 저장
            Cache.objectAuthorityCache.remove(serviceName);
            Cache.objectAuthorityCache.put(serviceName, toBeChangedServiceMap.get(serviceName));

//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            log.error("refreshServiceObjectCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    public ApiResponse createAuthorityCache() {
        try {
            List<Authority> authorityList = authorityMapper.selectAllAuthorities();
            Map<String, Map<String, Map<String, Authority>>> temporaryAuthorityCache = getAuthorityMap(authorityList);
            Cache.authorityUserCache = addUsers(temporaryAuthorityCache);
            return ApiResponseUtil.getSuccessApiResponse(Cache.authorityUserCache);
        } catch (Exception e) {
            log.error("createAuthorityCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Map<String, Map<String, Authority>>> getAuthorityMap(List<Authority> authorityList){
        Map<String, Map<String, Map<String, Authority>>> newAuthorityCacheMap = new HashMap<>();

        for (Authority authority : authorityList) {
            // service map이 없는 경우 생성
            String serviceName = authority.getServiceName();
            Map<String, Map<String, Authority>> serviceNameMap = newAuthorityCacheMap.get(serviceName);
            if (serviceNameMap == null) {
                serviceNameMap = new HashMap<>();
                newAuthorityCacheMap.put(serviceName, serviceNameMap);
            }

            // tenant map이 없는 경우 생성
            String tenantId = authority.getTenantId();
            Map<String, Authority> TenantIdMap = serviceNameMap.get(tenantId);
            if (TenantIdMap==null) {
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId,TenantIdMap);
            }

            // 맵에 오브젝트 저장
            TenantIdMap.put(authority.getAuthorityId(), authority);
        }
        return newAuthorityCacheMap;
    }

    private Map<String, Map<String, Map<String, Authority>>> addUsers(Map<String, Map<String, Map<String, Authority>>> authorityMap) {

        // 서비스 이름 keySet 및 iterator 세팅
        Set<String> serviceNameKeySet = authorityMap.keySet();
        Iterator<String> serviceNameKeyIterator = serviceNameKeySet.iterator();
        // 서비스 이름으로 반복문을 돌며 서브 오브젝트와 매핑된 권한 리스트를 오브젝트에 넣어준다.
        while (serviceNameKeyIterator.hasNext()) {

            String serviceName = serviceNameKeyIterator.next();
            Map<String,Map<String, Authority>> tenantIdMap = authorityMap.get(serviceName);
            if (tenantIdMap != null) {
                // 테넌트 아이디 keySet 및 iterator 세팅
                Set<String> tenantIdKeySet = tenantIdMap.keySet();
                Iterator<String> tenantIdKeyIterator = tenantIdKeySet.iterator();
                // 테넌트 아이디로 반복문을 돌며 서브 오브젝트와 매핑된 권한 리스트를 오브젝트에 넣어준다.
                while (tenantIdKeyIterator.hasNext()) {

                    String tenantId = tenantIdKeyIterator.next();
                    Map<String, Authority> authorityIdMap = tenantIdMap.get(tenantId);
                    if (authorityIdMap != null) {
                        // 권한 아이디 keySet 및 iterator 세팅
                        Set<String> authorityKeySet = authorityIdMap.keySet();
                        Iterator<String> authorityKeyIterator = authorityKeySet.iterator();

                        // 유저 맵핑 세팅
                        List<Map<String, Object>> authorityIdAndUserMapList = mappingMapper.selectAuthorityIdAndUserMapList(serviceName, tenantId);

                        // 권한 아이디로 반복문을 돌며 유저 리스트를 권한에 넣어준다.
                        while (authorityKeyIterator.hasNext()) {
                            String authorityId = authorityKeyIterator.next();
                            Authority authority = authorityIdMap.get(authorityId);
                            if (authority != null && authorityIdAndUserMapList != null) {

                                List<User> userList = getUserList(authorityId, authorityIdAndUserMapList);
                                authority.setAuthorityToUsers(userList);

                            }
                        }
                    }
                }
            }
        }

        return authorityMap;
    }

    private List<User> getUserList(String authorityId, List<Map<String, Object>> authorityIdAndUserMapList) {
        List<User> userList = new ArrayList<>();

        for (Map<String, Object> map : authorityIdAndUserMapList) {
            if (authorityId.equals(map.get("authorityId"))) {
                userList.add(getConvertingUser(map));
            }
        }

        return userList;
    }

    private User getConvertingUser(Map<String, Object> map) {
        User user = new User();

        user.setUserSequence((long) map.get("userSequence"));
        user.setUserId((String) map.get("userId"));
        user.setUserType((String) map.get("userType"));
        user.setTenantId((String) map.get("tenantId"));
        user.setServiceName((String) map.get("serviceName"));

        return user;
    }

    public ApiResponse refreshTenantAuthorityCache(String serviceName, String tenantId) {
        try {
            List<Authority> authorityList = authorityMapper.selectTenantAuthorities(serviceName, tenantId);
            Map<String, Map<String, Map<String, Authority>>> temporaryAuthorityCache = getAuthorityMap(authorityList);
            Map<String, Map<String, Map<String, Authority>>> toBeChangedTenantMap = addUsers(temporaryAuthorityCache);

            // 캐시 안에 기존 테넌트 맵 지우고 새로운 테넌트 맵 저장
            Map<String, Map<String, Authority>> cacheServiceMap = Cache.authorityUserCache.get(serviceName);
            cacheServiceMap.remove(tenantId);
            cacheServiceMap.put(tenantId, toBeChangedTenantMap.get(serviceName).get(tenantId));

//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.authorityUserCache);
        } catch (Exception e) {
            log.error("refreshTenantAuthorityCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse refreshServiceAuthorityCache(String serviceName) {
        try {
            List<Authority> authorityList = authorityMapper.selectServiceAuthorities(serviceName);
            Map<String, Map<String, Map<String, Authority>>> temporaryAuthorityCache = getAuthorityMap(authorityList);
            Map<String, Map<String, Map<String, Authority>>> toBeChangedServiceMap = addUsers(temporaryAuthorityCache);

            // 캐시 안에 기존 서비스 맵 지우고 새로운 서비스 맵 저장
            Cache.authorityUserCache.remove(serviceName);
            Cache.authorityUserCache.put(serviceName, toBeChangedServiceMap.get(serviceName));

//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.authorityUserCache);
        } catch (Exception e) {
            log.error("refreshServiceAuthorityCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse clearUserCache(){
        try {
            synchronized (Cache.userAuthorityCache) {
                Cache.userAuthorityCache.clear();
            }
//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.userAuthorityCache);
        } catch (Exception e) {
            log.error("clearUserCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse clearUserServiceCache(String serviceName){
        try {
            synchronized (Cache.userAuthorityCache) {
                if (Cache.userAuthorityCache.get(serviceName) != null) {
                    Cache.userAuthorityCache.get(serviceName).clear();
                }
            }
//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.userAuthorityCache);
        } catch (Exception e) {
            log.error("clearUserServiceCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse clearUserTenantCache(String serviceName, String tenantId) {
        try {
            if (tenantId == null) {
                tenantId = Constant.DEFAULT_TENANT_ID;
            }
            synchronized (Cache.userAuthorityCache) {
                if (Cache.userAuthorityCache.get(serviceName) != null) {
                    if (Cache.userAuthorityCache.get(serviceName).get(tenantId) != null) {
                        Cache.userAuthorityCache.get(serviceName).get(tenantId).clear();
                    }
                }
            }
//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(new Object());
        } catch (Exception e) {
            log.error("clearUserTenantCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    // todo 코드 캐싱 부모/자식 관계로 연결 시키는 로직 필요
    @PostConstruct
    public ApiResponse createCodeCache() {
        try {
            List<Code> codeList = codeMapper.selectAllCodes();
            Cache.codeCache = getCodes(codeList);
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            log.error("createCodeCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Map<String, Map<String, Code>>> getCodes(List<Code> codeList) {
        Map<String, Map<String, Map<String, Code>>> newCodeCacheMap = new HashMap<>();

        for (Code code : codeList){

            // service map이 없는 경우 생성
            String serviceName = code.getServiceName();
            Map<String, Map<String, Code>> serviceNameMap = newCodeCacheMap.get(serviceName);
            if (serviceNameMap == null) {
                serviceNameMap = new HashMap<>();
                newCodeCacheMap.put(serviceName, serviceNameMap);
            }

            // tenant map이 없는 경우 생성
            String tenantId = code.getTenantId();
            Map<String, Code> TenantIdMap = serviceNameMap.get(tenantId);
            if (TenantIdMap==null) {
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId,TenantIdMap);
            }

            // 맵에 오브젝트 저장 코드
            TenantIdMap.put(code.getCodeId(), code);
        }

        return newCodeCacheMap;
    }

    public ApiResponse refreshTenantCodeCache(String serviceName, String tenantId) {
        try {
            List<Code> codeList = codeMapper.selectTenantCodes(serviceName, tenantId);
            // TODO 기존 캐시를 지워버리기 떄문에 수정 필요
            Cache.codeCache = getCodes(codeList);
//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            log.error("refreshTenantCodeCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse refreshServiceCodeCache(String serviceName) {
        try {
            List<Code> codeList = codeMapper.selectServiceCodes(serviceName);
            // TODO 기존 캐시를 지워버리기 떄문에 수정 필요
            Cache.codeCache = getCodes(codeList);
//            networkService.sendProvideServersToInit();
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            log.error("refreshServiceCodeCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
}
