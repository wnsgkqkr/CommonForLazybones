package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.*;
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
    private ObjectService objectService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private CodeService codeService;

    // cfl 서비스의 캐시 갱신
    public ApiResponse cacheInit(CacheUpdateRequest cacheUpdateRequest) {
        try {
            String tenantId = cacheUpdateRequest.getTenantId();
            String serviceName = cacheUpdateRequest.getServiceName();
            if (tenantId == null) {
                tenantId = Constant.DEFAULT_TENANT_ID;
            }

            if ("object".equals(cacheUpdateRequest.getCacheType())) {
                refreshTenantObjectCache(serviceName, tenantId);
            } else if ("authority".equals(cacheUpdateRequest.getCacheType())) {
                refreshTenantAuthorityCache(serviceName, tenantId);
            } else if ("code".equals(cacheUpdateRequest.getCacheType())) {
                refreshTenantCodeCache(serviceName, tenantId);
            } else if ("user".equals(cacheUpdateRequest.getCacheType())) {
                clearUserTenantCache(serviceName, tenantId);
            } else if ("all".equals(cacheUpdateRequest.getCacheType())) {
                refreshTenantObjectCache(serviceName, tenantId);
                refreshTenantAuthorityCache(serviceName, tenantId);
                refreshTenantCodeCache(serviceName, tenantId);
                clearUserTenantCache(serviceName, tenantId);
            } else {
                throw new Exception("Invalid parameter in cacheInit.");
            }
            return ApiResponseUtil.getSuccessApiResponse(cacheUpdateRequest);
        } catch (Exception e) {
            log.error("cacheInit fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    public ApiResponse createObjectCache() {
        try {
            List<CflObject> objectList = objectService.getAllObjects();
            Map<String, Map<String, Map<String, CflObject>>> objectMap = getObjectMap(objectList);
            Map<String, Map<String, Map<String, CflObject>>> toBeChangedObjectCache = addSubObjectsAndAuthorities(objectMap);
            synchronized (Cache.objectAuthorityCache) {
                Cache.objectAuthorityCache = toBeChangedObjectCache;
            }
            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            log.error("createObjectCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Map<String, Map<String, CflObject>>> getObjectMap(List<CflObject> objectList) {
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
            if (TenantIdMap == null) {
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId, TenantIdMap);
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
            Map<String, Map<String, CflObject>> tenantIdMap = objectMap.get(serviceName);
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
                        List<Map<String, String>> objectIdAndSubObjectIdMapList = mappingService.getObjectIdAndSubObjectIdMapList(serviceName, tenantId);
                        List<Map<String, Object>> objectIdAndAuthorityMapList = mappingService.getObjectIdAndAuthorityMapList(serviceName, tenantId);

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
                                if (objectIdAndAuthorityMapList != null) {
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

        List<String> subObjectIdList = new ArrayList<>();

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

        authority.setAuthoritySequence((long) map.get("authoritySequence"));
        authority.setAuthorityId((String) map.get("authorityId"));
        authority.setAuthorityName((String) map.get("authorityName"));
        authority.setAuthorityType((String) map.get("authorityType"));
        authority.setTenantId((String) map.get("tenantId"));
        authority.setServiceName((String) map.get("serviceName"));

        return authority;
    }

    public ApiResponse refreshTenantObjectCache(String serviceName, String tenantId) {
        try {
            List<CflObject> objectList = objectService.getTenantObjects(serviceName, tenantId);
            Map<String, Map<String, Map<String, CflObject>>> objectMap = getObjectMap(objectList);
            Map<String, Map<String, Map<String, CflObject>>> toBeChangedTenantMap = addSubObjectsAndAuthorities(objectMap);

            // 캐시 안에 기존 테넌트 맵 지우고 새로운 테넌트 맵 저장
            Map<String, Map<String, Map<String, CflObject>>> temporaryCache = Cache.objectAuthorityCache;
            Map<String, Map<String, CflObject>> cacheServiceMap = temporaryCache.get(serviceName);

            synchronized (Cache.objectAuthorityCache) {
                cacheServiceMap.remove(tenantId);
                cacheServiceMap.put(tenantId, toBeChangedTenantMap.get(serviceName).get(tenantId));
            }

            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            log.error("refreshTenantObjectCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse refreshServiceObjectCache(String serviceName) {
        try {
            List<CflObject> objectList = objectService.getServiceObjects(serviceName);
            Map<String, Map<String, Map<String, CflObject>>> objectMap = getObjectMap(objectList);
            Map<String, Map<String, Map<String, CflObject>>> toBeChangedServiceMap = addSubObjectsAndAuthorities(objectMap);

            // 캐시 안에 기존 서비스 맵 지우고 새로운 서비스 맵 저장
            synchronized (Cache.objectAuthorityCache) {
                Cache.objectAuthorityCache.remove(serviceName);
                Cache.objectAuthorityCache.put(serviceName, toBeChangedServiceMap.get(serviceName));
            }

            return ApiResponseUtil.getSuccessApiResponse(Cache.objectAuthorityCache);
        } catch (Exception e) {
            log.error("refreshServiceObjectCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    @PostConstruct
    public ApiResponse createAuthorityCache() {
        try {
            List<Authority> authorityList = authorityService.getAllAuthorities();
            Map<String, Map<String, Map<String, Authority>>> authorityMap = getAuthorityMap(authorityList);
            Map<String, Map<String, Map<String, Authority>>> toBeChangedAuthorityCache = addUsers(authorityMap);
            synchronized (Cache.authorityUserCache) {
                Cache.authorityUserCache = toBeChangedAuthorityCache;
            }
            return ApiResponseUtil.getSuccessApiResponse(Cache.authorityUserCache);
        } catch (Exception e) {
            log.error("createAuthorityCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Map<String, Map<String, Authority>>> getAuthorityMap(List<Authority> authorityList) {
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
            if (TenantIdMap == null) {
                TenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId, TenantIdMap);
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
            Map<String, Map<String, Authority>> tenantIdMap = authorityMap.get(serviceName);
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
                        List<Map<String, Object>> authorityIdAndUserMapList = mappingService.getAuthorityIdAndUserMapList(serviceName, tenantId);

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
            List<Authority> authorityList = authorityService.getTenantAuthorities(serviceName, tenantId);
            Map<String, Map<String, Map<String, Authority>>> authorityMap = getAuthorityMap(authorityList);
            Map<String, Map<String, Map<String, Authority>>> toBeChangedTenantMap = addUsers(authorityMap);

            // 캐시 안에 기존 테넌트 맵 지우고 새로운 테넌트 맵 저장
            Map<String, Map<String, Authority>> cacheServiceMap = Cache.authorityUserCache.get(serviceName);

            synchronized (Cache.authorityUserCache) {
                cacheServiceMap.remove(tenantId);
                cacheServiceMap.put(tenantId, toBeChangedTenantMap.get(serviceName).get(tenantId));
            }

            return ApiResponseUtil.getSuccessApiResponse(Cache.authorityUserCache);
        } catch (Exception e) {
            log.error("refreshTenantAuthorityCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse refreshServiceAuthorityCache(String serviceName) {
        try {
            List<Authority> authorityList = authorityService.getServiceAuthorities(serviceName);
            Map<String, Map<String, Map<String, Authority>>> authorityMap = getAuthorityMap(authorityList);
            Map<String, Map<String, Map<String, Authority>>> toBeChangedServiceMap = addUsers(authorityMap);

            // 캐시 안에 기존 서비스 맵 지우고 새로운 서비스 맵 저장
            synchronized (Cache.authorityUserCache) {
                Cache.authorityUserCache.remove(serviceName);
                Cache.authorityUserCache.put(serviceName, toBeChangedServiceMap.get(serviceName));
            }

            return ApiResponseUtil.getSuccessApiResponse(Cache.authorityUserCache);
        } catch (Exception e) {
            log.error("refreshServiceAuthorityCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse clearUserCache() {
        try {
            synchronized (Cache.userAuthorityCache) {
                Cache.userAuthorityCache.clear();
            }
            return ApiResponseUtil.getSuccessApiResponse(Cache.userAuthorityCache);
        } catch (Exception e) {
            log.error("clearUserCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse clearUserServiceCache(String serviceName) {
        try {
            synchronized (Cache.userAuthorityCache) {
                if (Cache.userAuthorityCache.get(serviceName) != null) {
                    Cache.userAuthorityCache.get(serviceName).clear();
                }
            }
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
            List<Code> codeList = codeService.getAllCodes();
            Map<String, Map<String, Map<String, Code>>> codeMap = getCodeMap(codeList);
            Map<String, Map<String, Map<String, Code>>> toBeChangedCodeCache = setCodeMap(codeMap);
            synchronized (Cache.codeCache) {
                Cache.codeCache = toBeChangedCodeCache;
            }

            Map<String, Map<String, Map<String, Code>>> usingCodeMap = getCodeMap(codeList);
            Map<String, Map<String, Map<String, Code>>> toBeChangedUsingCodeCache = setUsingCodeMap(usingCodeMap);
            synchronized (Cache.usingCodeCache) {
                Cache.usingCodeCache = toBeChangedUsingCodeCache;
            }
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            log.error("createCodeCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Map<String, Map<String, Code>>> getCodeMap(List<Code> codeList) {
        Map<String, Map<String, Map<String, Code>>> newCodeCacheMap = new HashMap<>();

        for (Code code : codeList) {

            // service map이 없는 경우 생성
            String serviceName = code.getServiceName();
            Map<String, Map<String, Code>> serviceNameMap = newCodeCacheMap.get(serviceName);
            if (serviceNameMap == null) {
                serviceNameMap = new HashMap<>();
                newCodeCacheMap.put(serviceName, serviceNameMap);
            }

            // tenant map이 없는 경우 생성
            String tenantId = code.getTenantId();
            Map<String, Code> tenantIdMap = serviceNameMap.get(tenantId);
            if (tenantIdMap == null) {
                tenantIdMap = new HashMap<>();
                serviceNameMap.put(tenantId, tenantIdMap);
            }
        }

        return newCodeCacheMap;
    }

    private Map<String, Map<String, Map<String, Code>>> setCodeMap(Map<String, Map<String, Map<String, Code>>> codeMap) {

        // 서비스 이름 keySet 및 iterator 세팅
        Set<String> serviceNameKeySet = codeMap.keySet();
        Iterator<String> serviceNameKeyIterator = serviceNameKeySet.iterator();
        // 서비스 이름으로 반복문을 돌며 서브 코드 및 다국어 정보를 코드에 넣어준다.
        while (serviceNameKeyIterator.hasNext()) {

            String serviceName = serviceNameKeyIterator.next();
            Map<String, Map<String, Code>> tenantIdMap = codeMap.get(serviceName);
            if (tenantIdMap != null) {
                // 테넌트 아이디 keySet 및 iterator 세팅
                Set<String> tenantIdKeySet = tenantIdMap.keySet();
                Iterator<String> tenantIdKeyIterator = tenantIdKeySet.iterator();
                // 테넌트 아이디로 반복문을 돌며 서브 코드 및 다국어 정보를 코드에 넣어준다.
                while (tenantIdKeyIterator.hasNext()) {

                    String tenantId = tenantIdKeyIterator.next();
                    Map<String, Code> codeIdMap = tenantIdMap.get(tenantId);
                    if (codeIdMap != null) {
                        // 코드 정보 및 권한 매핑 정보 세팅
                        List<Map<String, String>> codeMultiLanguageMapList = codeService.getCodeMultiLanguageMapList(serviceName, tenantId);

                        // 최상위 코드들을 가져온다(매핑테이블엔 없고 코드테이블엔 있는 코드들)
                        List<Code> topLevelCodes = mappingService.getTopLevelCodes(serviceName, tenantId);

                        codeIdMap = createCodeMap(codeIdMap, topLevelCodes, "", ":", codeMultiLanguageMapList);
                        tenantIdMap.put(tenantId, codeIdMap);
                    }
                }
            }
        }

        return codeMap;
    }

    // setCodeMap()과 동일 사용중인 코드만 가져오는
    private Map<String, Map<String, Map<String, Code>>> setUsingCodeMap(Map<String, Map<String, Map<String, Code>>> codeMap) {

        Set<String> serviceNameKeySet = codeMap.keySet();
        Iterator<String> serviceNameKeyIterator = serviceNameKeySet.iterator();
        while (serviceNameKeyIterator.hasNext()) {

            String serviceName = serviceNameKeyIterator.next();
            Map<String, Map<String, Code>> tenantIdMap = codeMap.get(serviceName);
            if (tenantIdMap != null) {
                Set<String> tenantIdKeySet = tenantIdMap.keySet();
                Iterator<String> tenantIdKeyIterator = tenantIdKeySet.iterator();
                while (tenantIdKeyIterator.hasNext()) {

                    String tenantId = tenantIdKeyIterator.next();
                    Map<String, Code> codeIdMap = tenantIdMap.get(tenantId);
                    if (codeIdMap != null) {
                        List<Map<String, String>> codeMultiLanguageMapList = codeService.getCodeMultiLanguageMapList(serviceName, tenantId);

                        List<Code> topLevelCodes = mappingService.getUsingTopLevelCodes(serviceName, tenantId);

                        codeIdMap = createUsingCodeMap(codeIdMap, topLevelCodes, "", ":", codeMultiLanguageMapList);
                        tenantIdMap.put(tenantId, codeIdMap);
                    }
                }
            }
        }

        return codeMap;
    }

    // fullDepth = PID:PID:PID:PID:....:ID , fullSequence = :PSEQ:PSEQ:PSEQ:....:SEQ -> 순환이 안생기게 시퀀스로 :로 나눠서 판별(무한루프)
    // 자기 자신에 해당하는 ID만들고 자식 있을시 매핑테이블에서 찾아서 자신 + 된 문자열 넘겨줘서 풀시퀀스 풀뎁스 완성
    private Map<String, Code> createCodeMap(Map<String, Code> codeMap, List<Code> highLevelCodes, String fullDepth, String fullSequence, List<Map<String, String>> codeMultiLanguageMapList) {
        for (Code highLevelCode : highLevelCodes) {
            String nextFullDepth = fullDepth + highLevelCode.getCodeId() + ":";
            String nextFullSequence = fullSequence + highLevelCode.getCodeSequence() + ":";
            List<Code> lowLevelCodes = mappingService.getLowLevelCodes(highLevelCode);

            codeMap = putCodeMap(codeMap, highLevelCode, lowLevelCodes, codeMultiLanguageMapList, nextFullDepth, nextFullSequence);

            createCodeMap(codeMap, lowLevelCodes, nextFullDepth, nextFullSequence, codeMultiLanguageMapList);
        }
        return codeMap;
    }

    // createCodeMap과 동일 사용중인애들만 저장
    private Map<String, Code> createUsingCodeMap(Map<String, Code> codeMap, List<Code> highLevelCodes, String fullDepth, String fullSequence, List<Map<String, String>> codeMultiLanguageMapList) {
        for (Code highLevelCode : highLevelCodes) {
            String nextFullDepth = fullDepth + highLevelCode.getCodeId() + ":";
            String nextFullSequence = fullSequence + highLevelCode.getCodeSequence() + ":";
            List<Code> lowLevelCodes = mappingService.getUsingLowLevelCodes(highLevelCode);

            codeMap = putCodeMap(codeMap, highLevelCode, lowLevelCodes, codeMultiLanguageMapList, nextFullDepth, nextFullSequence);

            createUsingCodeMap(codeMap, lowLevelCodes, nextFullDepth, nextFullSequence, codeMultiLanguageMapList);
        }
        return codeMap;
    }

    // 코드 오브젝트에 서브코드들, 다국어맵, 풀시퀀스 세팅해주고 캐시가 될 맵에 넣어준다.
    private Map<String, Code> putCodeMap(Map<String, Code> codeMap, Code code, List<Code> subCodes, List<Map<String, String>> codeMultiLanguageMapList, String fullDepth, String fullSequence) {

        Map<String, Code> subCodeMap = new HashMap<>();
        for (Code subCode : subCodes) {
            subCodeMap.put(subCode.getCodeId(), subCode);
        }
        code.setSubCodes(subCodeMap);

        Map<String, String> multiLanguageMap = getMultiLanguageMap(code.getCodeSequence(), codeMultiLanguageMapList);
        code.setMultiLanguageMap(multiLanguageMap);

        code.setFullSequencePath(fullSequence);
        codeMap.put(fullDepth, code);
        return codeMap;
    }

    private Map<String, String> getMultiLanguageMap(Long codeSequence, List<Map<String, String>> codeMultiLanguageMapList) {
        Map<String, String> multiLanguageMap = new HashMap<>();

        for (Map<String, String> map : codeMultiLanguageMapList) {

            if (codeSequence.equals(map.get("codeSequence"))) {
                multiLanguageMap.put(map.get("nation"), map.get("multiLanguageName"));
            }
        }

        return multiLanguageMap;
    }


    public ApiResponse refreshTenantCodeCache(String serviceName, String tenantId) {
        try {
            List<Code> codeList = codeService.getTenantCodes(serviceName, tenantId);
            Map<String, Map<String, Map<String, Code>>> codeMap = getCodeMap(codeList);
            Map<String, Map<String, Map<String, Code>>> toBeChangedTenantMap = setCodeMap(codeMap);
            Map<String, Map<String, Map<String, Code>>> usingCodeMap = getCodeMap(codeList);
            Map<String, Map<String, Map<String, Code>>> toBeChangedTenantUsingMap = setUsingCodeMap(usingCodeMap);


            // 캐시 안에 기존 테넌트 맵 지우고 새로운 테넌트 맵 저장
            Map<String, Map<String, Map<String, Code>>> temporaryCache = Cache.codeCache;
            Map<String, Map<String, Code>> cacheServiceMap = temporaryCache.get(serviceName);
            Map<String, Map<String, Map<String, Code>>> temporaryUsingCache = Cache.usingCodeCache;
            Map<String, Map<String, Code>> cacheServiceUsingMap = temporaryUsingCache.get(serviceName);

            synchronized (Cache.codeCache) {
                cacheServiceMap.remove(tenantId);
                cacheServiceMap.put(tenantId, toBeChangedTenantMap.get(serviceName).get(tenantId));
            }
            synchronized (Cache.usingCodeCache) {
                cacheServiceUsingMap.remove(tenantId);
                cacheServiceUsingMap.put(tenantId, toBeChangedTenantUsingMap.get(serviceName).get(tenantId));
            }

            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            log.error("refreshTenantCodeCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse refreshServiceCodeCache(String serviceName) {
        try {
            List<Code> codeList = codeService.getServiceCodes(serviceName);
            Map<String, Map<String, Map<String, Code>>> codeMap = getCodeMap(codeList);
            Map<String, Map<String, Map<String, Code>>> toBeChangedServiceMap = setCodeMap(codeMap);
            Map<String, Map<String, Map<String, Code>>> usingCodeMap = getCodeMap(codeList);
            Map<String, Map<String, Map<String, Code>>> toBeChangedServiceUsingMap = setUsingCodeMap(usingCodeMap);

            // 캐시 안에 기존 서비스 맵 지우고 새로운 서비스 맵 저장
            synchronized (Cache.codeCache) {
                Cache.codeCache.remove(serviceName);
                Cache.codeCache.put(serviceName, toBeChangedServiceMap.get(serviceName));
            }

            synchronized (Cache.usingCodeCache) {
                Cache.usingCodeCache.remove(serviceName);
                Cache.usingCodeCache.put(serviceName, toBeChangedServiceUsingMap.get(serviceName));
            }
            return ApiResponseUtil.getSuccessApiResponse(Cache.codeCache);
        } catch (Exception e) {
            log.error("refreshServiceCodeCache fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
}
