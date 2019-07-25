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
        try {
            authority.setServiceName(serviceName);
            authority.setTenantId(tenantId);
            authority.setAuthorityId(authorityId);

            authorityMapper.insertAuthority(authority);
            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse);
            return successApiResponse;
        } catch (Exception e) { // TODO Exception 정해서 코드 정하고 넘겨주기
            log.error("createAuthority fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse modifyAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        try {
            authority.setServiceName(serviceName);
            authority.setTenantId(tenantId);
            authority.setAuthorityId(authorityId);

            authorityMapper.updateAuthority(authority);
            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse);
            return successApiResponse;
        } catch (Exception e) { // TODO 실패도 히스토리추가(오류이후)
            log.error("modifyAuthority fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthority(String serviceName, String tenantId, String authorityId) {
        try {
            Authority authority = new Authority(serviceName, tenantId, authorityId);

            // 권한 삭제 전 매핑 정보부터 우선 삭제 후 권한 삭제 진행
            mappingService.removeAuthorityMapping(authority);
            authorityMapper.deleteAuthority(authority);

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("modifyAuthority fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getAuthority(String serviceName, String tenantId, String authorityId){
        try {
            Authority authority = new Authority(serviceName, tenantId, authorityId);

            Authority authorityFromCache = Cache.authorityUserCache.get(serviceName).get(authority.getTenantId()).get(authorityId);

            ApiResponse apiResponse;

            // 캐시에 오브젝트가 없는 경우
            if (authorityFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(authorityFromCache);
            }

            return apiResponse;
        } catch (Exception e) {
            log.error("getAuthority fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse createAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        try {
            Authority authority = new Authority(serviceName, tenantId, authorityId);
            List<User> duplicatedUserList = new ArrayList<>();

            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                requestUser = userService.checkExistAndCreateUser(requestUser);

                if (mappingService.isExistAuthorityUserMapping(authorityId, requestUser)) {
                    duplicatedUserList.add(requestUser);
                } else {
                    mappingService.createAuthorityUserMappting(authorityId, requestUser);
                }
            }

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));

            ApiResponse apiResponse;

            if (duplicatedUserList.size() == 0) {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
            } else {
                apiResponse =  ApiResponseUtil.getDuplicateApiResponse(duplicatedUserList);
            }

            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, apiResponse);
            return apiResponse;
        } catch (Exception e) {
            log.error("createAuthorityUsersMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        try {
            Authority authority = new Authority(serviceName, tenantId, authorityId);
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                mappingService.removeAuthorityUserMapping(authorityId, requestUser);
            }
            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "authority"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("removeAuthorityUsersMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getAuthorityUsersMapping(String serviceName, String tenantId, String authorityId) {
        try {
            Authority authority = new Authority(serviceName, tenantId, authorityId);

            List<User> userList = Cache.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId()).get(authority.getAuthorityId()).getAuthorityToUsers();

            ApiResponse apiResponse;

            // 캐시에 권한 유저리스트가 없는 경우
            if (userList == null) {
                apiResponse = ApiResponseUtil.getMissingValueResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(userList);
            }

            return apiResponse;
        } catch (Exception e) {
            log.error("getAuthorityUsersMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getTenantAuthorityList(String serviceName, String tenantId) {
        try {
            Authority authority = new Authority(serviceName, tenantId);

            Map<String, Authority> authorityMap = Cache.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId());

            if (authorityMap != null) {
                List<Authority> tenantAuthorities = new ArrayList<>(authorityMap.values());
                return ApiResponseUtil.getSuccessApiResponse(tenantAuthorities);
            } else {
                List<Authority> tenantAuthorities = authorityMapper.selectTenantAuthorities(serviceName, tenantId);
                return ApiResponseUtil.getSuccessApiResponse(tenantAuthorities);
            }
        } catch (Exception e) {
            log.error("getTenantAuthorityList fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public Authority checkExistAndCreateAuthority(Authority needCheckAuthority){
        Authority getAuthority = authorityMapper.selectAuthority(needCheckAuthority);
        if (getAuthority != null) {
            return getAuthority;
        } else {
            authorityMapper.insertAuthority(needCheckAuthority);
            return needCheckAuthority;
        }
    }
}
