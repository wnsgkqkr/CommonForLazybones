package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.CacheUpdateRequest;
import com.cfl.domain.Code;
import com.cfl.mapper.CodeMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CodeService{
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private NetworkService networkService;
    @Autowired
    private HistoryService historyService;

    private static final String DEFAULT_PARENT_CODE_ID = "NOT_TO_EXIST";

    public List<Code> getAllCodes() {
        return codeMapper.selectAllCodes();
    }

    public List<Code> getServiceCodes(String serviceName) {
        return codeMapper.selectServiceCodes(serviceName);
    }

    public List<Code> getTenantCodes(String serviceName, String tenantId) {
        return codeMapper.selectTenantCodes(serviceName, tenantId);
    }

    public List<Map<String, String>> getCodeMultiLanguageMapList(String serviceName, String tenantId) {
        return codeMapper.selectCodeMultiLanguageMapList(serviceName, tenantId);
    }

    public ApiResponse createCode(String serviceName, String tenantId, String codeId, Code code) {
        try {
            code.setServiceName(serviceName);
            code.setTenantId(tenantId);
            code.setCodeId(codeId);
            code.setMultiLanguageCode(UUID.randomUUID().toString());

            // 부모 코드 아이디가 기본키에 포함되기 때문에 null인 경우 디폴트 부모 코드 아이디를 추가한다.
            if (code.getParentCodeId() == null) {
                code.setParentCodeId(DEFAULT_PARENT_CODE_ID);
            }
            // 부모 코드아이디와 자신의 아이디가 같을때 리턴
            if (code.getParentCodeId().equals(code.getCodeId())) {
                return ApiResponseUtil.getFailureApiResponse();
            }
            codeMapper.insertCode(code);
            codeMapper.insertCodeMultiLanguage(code.getMultiLanguageCode(), code.getMultiLanguageMap());

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "code"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, code.getTenantId(), code, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("createCode fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyCode(String serviceName, String tenantId, String codeId, Code code) {
        try {
            code.setServiceName(serviceName);
            code.setTenantId(tenantId);
            code.setCodeId(codeId);

            // 부모 코드아이디와 자신의 아이디가 같을때 리턴
            if (code.getParentCodeId().equals(code.getCodeId())) {
                return ApiResponseUtil.getFailureApiResponse();
            }
            // 다국어 정보의 경우 기존 데이터를 지우고 새로 생성한다.
            codeMapper.updateCode(code);
            codeMapper.deleteCodeMultiLanguage(code.getMultiLanguageCode());
            codeMapper.insertCodeMultiLanguage(code.getMultiLanguageCode(), code.getMultiLanguageMap());

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "code"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, code.getTenantId(), code, successApiResponse);
            return successApiResponse;
        } catch(Exception e) {
            log.error("modifyCode fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeCode(String serviceName, String tenantId, String codeId) {
        try {
            Code code = new Code(serviceName, tenantId, codeId);
            code = codeMapper.selectCode(code);

            codeMapper.deleteCodeMultiLanguage(code.getMultiLanguageCode());
            codeMapper.deleteCode(code);

            networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId , "code"));
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, code.getTenantId(), code, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("removeCode fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getCode(String serviceName, String tenantId, String codeId) {
        try {
            Code code = new Code(serviceName, tenantId, codeId);

            Code codeFromCache = Cache.codeCache.get(serviceName).get(code.getTenantId()).get(codeId);

            ApiResponse apiResponse;

            // 캐시에 코드가 없는 경우
            if (codeFromCache == null) {
                apiResponse = ApiResponseUtil.getMissingValueApiResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(codeFromCache);
            }

            return apiResponse;
        } catch (Exception e) {
            log.error("getCode fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getTenantCodeList(String serviceName, String tenantId) {
        try {
            Code code = new Code(serviceName, tenantId);

            Map<String, Code> codeMap = Cache.codeCache.get(code.getServiceName()).get(code.getTenantId());
            if (codeMap != null) {
                List<Code> tenantCodes = new ArrayList<>(codeMap.values());
                return ApiResponseUtil.getSuccessApiResponse(tenantCodes);
            } else {
                List<Code> tenantCodes = codeMapper.selectTenantCodes(serviceName, tenantId);
                return ApiResponseUtil.getSuccessApiResponse(tenantCodes);
            }
        } catch (Exception e) {
            log.error("getTenantCodeList fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
}
