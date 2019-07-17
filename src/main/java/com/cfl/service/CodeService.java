package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Code;
import com.cfl.mapper.CodeMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CodeService{
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

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

            codeMapper.insertCode(code);
            codeMapper.insertCodeMultiLanguage(code.getMultiLanguageCode(), code.getMultiLanguageMap());

            cacheService.refreshTenantCodeCache(serviceName, code.getTenantId());
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

            // 다국어 정보의 경우 기존 데이터를 지우고 새로 생성한다.
            codeMapper.updateCode(code);
            codeMapper.deleteCodeMultiLanguage(code.getMultiLanguageCode());
            codeMapper.insertCodeMultiLanguage(code.getMultiLanguageCode(), code.getMultiLanguageMap());

            cacheService.refreshTenantCodeCache(serviceName, code.getTenantId());
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

            cacheService.refreshTenantCodeCache(serviceName, code.getTenantId());
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
                apiResponse = ApiResponseUtil.getMissingValueResponse();
            } else {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(codeFromCache);
            }

            return apiResponse;
        } catch (Exception e) {
            log.error("getCode fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
}
