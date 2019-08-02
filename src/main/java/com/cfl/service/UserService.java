package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.CacheUpdateRequest;
import com.cfl.domain.User;
import com.cfl.mapper.UserMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private NetworkService networkService;
    @Autowired
    private HistoryService historyService;

    public ApiResponse createUser(String serviceName, String tenantId, String userId, User user) {
        ApiResponse apiResponse;

        try {
            user.setServiceName(serviceName);
            user.setTenantId(tenantId);
            user.setUserId(userId);

            User selectedUser = userMapper.selectUser(user);

            // 유저 중복 생성의 경우
            if (selectedUser != null) {
                apiResponse = ApiResponseUtil.getDuplicateCreationApiResponse();
            } else {
                userMapper.insertUser(user);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "user"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            }
        } catch (Exception e) {
            log.error("createUser fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, user.getTenantId(), user, apiResponse);
        return apiResponse;
    }

    public ApiResponse modifyUser(String serviceName, String tenantId, String userId, User user) {
        ApiResponse apiResponse;

        try {
            user.setServiceName(serviceName);
            user.setTenantId(tenantId);
            user.setUserId(userId);

            User selectedUser = userMapper.selectUser(user);

            // 존재하지 않는 유저를 수정하려는 경우
            if (selectedUser == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                userMapper.updateUser(user);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "user"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            }
        } catch (Exception e) {
            log.error("modifyUser fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, user.getTenantId(), user, apiResponse);
        return apiResponse;
    }

    public ApiResponse removeUser(String serviceName, String tenantId, String userId) {
        ApiResponse apiResponse;
        User user = new User(serviceName, tenantId, userId);

        try {
            User selectedUser = userMapper.selectUser(user);

            // 존재하지 않는 유저을 수정하려는 경우
            if (selectedUser == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                userMapper.deleteUser(user);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "user"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            }
        } catch (Exception e) {
            log.error("removeUser fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, user.getTenantId(), user, apiResponse);
        return apiResponse;
    }

    public ApiResponse getUser(String serviceName, String tenantId, String userId) {
        ApiResponse apiResponse;
        User user = new User(serviceName, tenantId, userId);

        try {
            User userFromCache = getUserFromCache(user);

            // 캐시에 유저가 존재할 경우 캐시에서 가져온 유저 반환, 없을 경우 DB에서 가져와 캐시에 저장 후 반환
            if (userFromCache != null) {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(userFromCache);
            } else {
                User userFromDB = getUserFromDBAndSaveCache(user);

                // DB에도 없는 경우
                if (userFromDB == null) {
                    apiResponse = ApiResponseUtil.getMissingValueApiResponse();
                } else {
                    apiResponse = ApiResponseUtil.getSuccessApiResponse(userFromDB);
                }
            }
        } catch (Exception e) {
            log.error("getUser fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    public ApiResponse getUserAuthoritiesMapping(String serviceName, String tenantId, String userId) {
        ApiResponse apiResponse;
        User user = new User(serviceName, tenantId, userId);

        try {
            List<Authority> authorityList = getUserToAuthoritiesFromCache(user);

            // 캐시에 유저 권한리스트가 존재할 경우 캐시에서 가져온 권한리스트 반환, 없을 경우 DB에서 가져와 캐시에 저장 후 반환
            if (authorityList != null) {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authorityList);
            } else {
                User userFromDB = getUserFromDBAndSaveCache(user);

                // DB에도 없는 경우
                if (userFromDB == null) {
                    apiResponse = ApiResponseUtil.getSuccessApiResponse(Collections.emptyList());
                } else {
                    apiResponse = ApiResponseUtil.getSuccessApiResponse(userFromDB.getUserToAuthorities());
                }
            }
        } catch (Exception e) {
            log.error("getUserAuthoritiesMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    private User getUserFromDBAndSaveCache(User user) {
        User userFromDB = userMapper.selectUser(user);

        // DB에도 없는 경우 null 반환
        if (userFromDB == null) {
            return null;
        }

        List<Authority> authorityListFromDB = mappingService.getUserAuthorities(userFromDB);

        if (authorityListFromDB == null) {
            authorityListFromDB = Collections.emptyList();
        }
        userFromDB.setUserToAuthorities(authorityListFromDB);

        // 캐시에 저장
        synchronized (Cache.userAuthorityCache) {
            Cache.userAuthorityCache.get(userFromDB.getServiceName()).get(userFromDB.getTenantId()).put(userFromDB.getUserId(), userFromDB);
        }

        return userFromDB;
    }

    private Map<String, User> getTenantUserMapFromCache(User user) {
        // User 캐시의 경우 미리 세팅을 안하기 때문에 캐시에 없는 경우 Map을 생성하여 반환한다.
        String serviceName = user.getServiceName();
        Map<String, Map<String, User>> serviceMapFromCache = Cache.userAuthorityCache.get(serviceName);
        if (serviceMapFromCache == null) {
            serviceMapFromCache = new HashMap<>();
            synchronized (Cache.userAuthorityCache) {
                Cache.userAuthorityCache.put(serviceName, serviceMapFromCache);
            }
        }

        String tenantId = user.getTenantId();
        Map<String, User> TenantMapFromCache = serviceMapFromCache.get(tenantId);
        if (TenantMapFromCache == null) {
            TenantMapFromCache = new HashMap<>();
            synchronized (Cache.userAuthorityCache) {
                Cache.userAuthorityCache.get(serviceName).put(tenantId, TenantMapFromCache);
            }
        }

        return TenantMapFromCache;
    }

    private User getUserFromCache(User user) {
        // 캐시에서 찾는 유저가 없는 경우 null 반환
        Map<String, User> tenantMapFromCache = getTenantUserMapFromCache(user);
        if (tenantMapFromCache == null) {
            return null;
        }

        return tenantMapFromCache.get(user.getUserId());
    }

    private List<Authority> getUserToAuthoritiesFromCache(User user) {
        User UserFromCache = getUserFromCache(user);
        if (UserFromCache == null) {
            return null;
        }

        return UserFromCache.getUserToAuthorities();
    }

    public User checkExistUserAndCreateUser(User userWhoNeedConfirmation) {
        User selectedUser = userMapper.selectUser(userWhoNeedConfirmation);

        if (selectedUser != null) {
            return selectedUser;
        } else {
            userMapper.insertUser(userWhoNeedConfirmation);
            return userWhoNeedConfirmation;
        }
    }
}
