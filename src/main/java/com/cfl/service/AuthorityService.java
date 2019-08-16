package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.*;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuthorityService{
    @Autowired
    private AuthorityMapper authorityMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private NetworkService networkService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private MappingService mappingService;

    public List<Authority> getAllAuthorities() {
        return authorityMapper.selectAllAuthorities();
    }

    public List<Authority> getServiceAuthorities(String serviceName) {
        return authorityMapper.selectServiceAuthorities(serviceName);
    }

    public List<Authority> getTenantAuthorities(String serviceName, String tenantId) {
        return authorityMapper.selectTenantAuthorities(serviceName, tenantId);
    }

    public ApiResponse createAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        ApiResponse apiResponse;

        try {
            authority.setServiceName(serviceName);
            authority.setTenantId(tenantId);
            authority.setAuthorityId(authorityId);

            Authority selectedAuthority = authorityMapper.selectAuthority(authority);

            // 권한 중복 생성의 경우
            if (selectedAuthority != null) {
                apiResponse = ApiResponseUtil.getDuplicateCreationApiResponse();
            } else {
                authorityMapper.insertAuthority(authority);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            }
        } catch (Exception e) {
            log.error("createAuthority fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, authority.getTenantId(), authority, apiResponse);
        return apiResponse;
    }

    public ApiResponse modifyAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        ApiResponse apiResponse;

        try {
            authority.setServiceName(serviceName);
            authority.setTenantId(tenantId);
            authority.setAuthorityId(authorityId);

            Authority selectedAuthority = authorityMapper.selectAuthority(authority);

            // 존재하지 않는 권한을 수정하려는 경우
            if (selectedAuthority == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                authorityMapper.updateAuthority(authority);
                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            }
        } catch (Exception e) {
            log.error("modifyAuthority fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, authority.getTenantId(), authority, apiResponse);
        return apiResponse;
    }

    public ApiResponse removeAuthority(String serviceName, String tenantId, String authorityId) {
        ApiResponse apiResponse;
        Authority authority = new Authority(serviceName, tenantId, authorityId);

        try {
            Authority selectedAuthority = authorityMapper.selectAuthority(authority);

            // 존재하지 않는 권한을 삭제하려는 경우
            if (selectedAuthority == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                // 권한 삭제 전 매핑 정보부터 우선 삭제 후 권한 삭제 진행
                mappingService.removeAuthorityMapping(authority);
                authorityMapper.deleteAuthority(authority);

                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            }
        } catch (Exception e) {
            log.error("modifyAuthority fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, authority.getTenantId(), authority, apiResponse);
        return apiResponse;
    }

    public ApiResponse getAuthority(String serviceName, String tenantId, String authorityId){
        ApiResponse apiResponse;
        Authority authority = new Authority(serviceName, tenantId, authorityId);

        try {
            Authority authorityFromCache = getAuthorityFromCache(authority);

            // 캐시에 권한이 없는 경우
            if (authorityFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authorityFromCache);
            }
        } catch (Exception e) {
            log.error("getAuthority fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    public ApiResponse createAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        ApiResponse apiResponse;
        Authority authority = new Authority(serviceName, tenantId, authorityId);

        try {
            Authority authorityFromCache = getAuthorityFromCache(authority);

            // 권한이 없는 경우
            if (authorityFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                List<User> duplicatedUserList = new ArrayList<>();

                for (User requestUser : requestUsers) {
                    requestUser.setServiceName(serviceName);
                    requestUser.setTenantId(authority.getTenantId());
                    requestUser = userService.checkExistUserAndCreateUser(requestUser);

                    // 이미 매핑이 되어있는 경우 중복유저리스트에 추가한다.
                    if (mappingService.isExistAuthorityUserMapping(authorityId, requestUser)) {
                        duplicatedUserList.add(requestUser);
                    } else {
                        mappingService.createAuthorityUserMappting(authorityId, requestUser);
                    }
                }

                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));

                if (duplicatedUserList.size() == 0) {
                    apiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
                } else {
                    apiResponse =  ApiResponseUtil.getDuplicateMappingApiResponse(duplicatedUserList);
                }
            }
        } catch (Exception e) {
            log.error("createAuthorityUsersMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, apiResponse);
        return apiResponse;
    }

    public ApiResponse removeAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        ApiResponse apiResponse;
        Authority authority = new Authority(serviceName, tenantId, authorityId);

        try {
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                mappingService.removeAuthorityUserMapping(authorityId, requestUser);
            }
            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
            apiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
        } catch (Exception e) {
            log.error("removeAuthorityUsersMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, apiResponse);
        return apiResponse;
    }

    public ApiResponse getAuthorityUsersMapping(String serviceName, String tenantId, String authorityId) {
        ApiResponse apiResponse;
        Authority authority = new Authority(serviceName, tenantId, authorityId);

        try {
            List<User> userList = getAuthorityToUsersFromCache(authority);

            // 캐시에 권한의 유저리스트가 없는 경우
            if (userList == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(userList);
            }
        } catch (Exception e) {
            log.error("getAuthorityUsersMapping fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    public ApiResponse getTenantAuthorityMap(String serviceName, String tenantId) {
        ApiResponse apiResponse;
        Authority authority = new Authority(serviceName, tenantId);

        try {
            Map<String, Authority> tenantAuthorityMap = getTenantAuthorityMapFromCache(authority);

            // 테넌트 맵이 없는 경우
            if (tenantAuthorityMap == null) {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(new HashMap<String, Authority>());
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(tenantAuthorityMap);
            }
        } catch (Exception e) {
            log.error("getTenantAuthorityList fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }

        return apiResponse;
    }

    private Map<String, Authority> getTenantAuthorityMapFromCache(Authority authority) {
        // 캐시에서 찾는 맵이 없는 경우 null 반환
        Map<String, Map<String, Authority>> serviceMapFromCache = Cache.authorityUserCache.get(authority.getServiceName());
        if (serviceMapFromCache == null) {
            return null;
        }

        return serviceMapFromCache.get(authority.getTenantId());
    }

    public Authority getAuthorityFromCache(Authority authority) {
        // 캐시에서 찾는 권한이 없는 경우 null 반환
        Map<String, Authority> tenantMapFromCache = getTenantAuthorityMapFromCache(authority);
        if (tenantMapFromCache == null) {
            return null;
        }

        return tenantMapFromCache.get(authority.getAuthorityId());
    }

    private List<User> getAuthorityToUsersFromCache(Authority authority) {
        Authority authorityFromCache = getAuthorityFromCache(authority);
        if (authorityFromCache == null) {
            return null;
        }

        return authorityFromCache.getAuthorityToUsers();
    }
}
