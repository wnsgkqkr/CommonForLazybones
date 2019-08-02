package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.customexception.ExistCodeException;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.CacheUpdateRequest;
import com.cfl.domain.Code;
import com.cfl.mapper.CodeMapper;
import com.cfl.mapper.MappingMapper;
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
    @Autowired
    private MappingService mappingService;

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

    public ApiResponse createCode(String serviceName, String tenantId, String[] fullIdPath, Code code) {

        ApiResponse apiResponse;
        try {
            code.setServiceName(serviceName);
            code.setTenantId(tenantId);
            //fullIdPath last element = codeId
            code.setCodeId(fullIdPath[fullIdPath.length - 1]);
            code.setMultiLanguageCode(UUID.randomUUID().toString());

            if (isExistCode(serviceName, tenantId, fullIdPath)) {
                apiResponse = ApiResponseUtil.getFailureApiResponse();
            } else {

                codeMapper.insertCode(code);
                codeMapper.insertCodeMultiLanguage(code.getMultiLanguageCode(), code.getMultiLanguageMap());

                String fullDepth = getFullDepth(fullIdPath);
                String highLevelFullDepth = null;
                if (fullDepth.length() > code.getCodeId().length() + 1) {
                    highLevelFullDepth = fullDepth.substring(0, fullDepth.length() - (code.getCodeId().length() + 1));
                }
                if (highLevelFullDepth != null) {
                    mappingService.createCodeSequenceAndSubCodeSequenceMapping(Cache.codeCache.get(serviceName).get(tenantId).get(highLevelFullDepth).getCodeSequence(), code.getCodeSequence());
                }

                networkService.sendProvideServersToInit("cfl", new CacheUpdateRequest(serviceName, tenantId, "code"));
                apiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            }
        } catch (Exception e) {
            log.error("createCode fail", e);
            apiResponse = ApiResponseUtil.getFailureApiResponse();
        }
        historyService.createHistory(serviceName, code.getTenantId(), code, apiResponse);
        return apiResponse;
    }

    public ApiResponse createCodeMapping(String serviceName, String tenantId, Long codeSequence, Long subCodeSequence) {
        try {
            mappingService.createCodeSequenceAndSubCodeSequenceMapping(codeSequence, subCodeSequence);
            Code code = new Code(codeSequence);
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, tenantId, code, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("createCodeMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyCode(String serviceName, String tenantId, String[] fullIdPath, Code code) {
        try {
            code.setServiceName(serviceName);
            code.setTenantId(tenantId);
            code.setCodeSequence(Cache.codeCache.get(serviceName).get(tenantId).get(getFullDepth(fullIdPath)).getCodeSequence());

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

    public ApiResponse removeCode(String serviceName, String tenantId, String[] fullIdPath) {
        try {
            Code code = new Code(serviceName, tenantId, Cache.codeCache.get(serviceName).get(tenantId).get(getFullDepth(fullIdPath)).getCodeSequence());
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

    public ApiResponse getCode(String serviceName, String tenantId, String[] fullIdPath) {
        try {
            String fullDepth = getFullDepth(fullIdPath);

            Code codeFromCache = Cache.codeCache.get(serviceName).get(tenantId).get(fullDepth);

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

    public ApiResponse getUsingCode(String serviceName, String tenantId, String[] fullIdPath) {
        try {
            String fullDepth = getFullDepth(fullIdPath);

            Code codeFromCache = Cache.usingCodeCache.get(serviceName).get(tenantId).get(fullDepth);

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

    public ApiResponse getTenantCodeList(String serviceName, String tenantId) {
        try {
            Code code = new Code(serviceName, tenantId);

            Map<String, Code> codeMap = Cache.codeCache.get(code.getServiceName()).get(code.getTenantId());
            if (codeMap != null) {
                return ApiResponseUtil.getSuccessApiResponse(codeMap);
            } else {
                return ApiResponseUtil.getMissingValueResponse();
            }
        } catch (Exception e) {
            log.error("getTenantCodeList fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getUsingTenantCodeList(String serviceName, String tenantId) {
        try {
            Code code = new Code(serviceName, tenantId);

            Map<String, Code> codeMap = Cache.usingCodeCache.get(code.getServiceName()).get(code.getTenantId());
            if (codeMap != null) {
                return ApiResponseUtil.getSuccessApiResponse(codeMap);
            } else {
                return ApiResponseUtil.getMissingValueResponse();
            }
        } catch (Exception e) {
            log.error("getTenantCodeList fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private boolean isExistCode(String serviceName, String tenantId, String[] fullIdPath) {
        String fullDepth = getFullDepth(fullIdPath);
        if(Cache.codeCache.get(serviceName).get(tenantId).get(fullDepth) != null) {
            return true;
        }
        return false;
    }

    //urlPath = PID/PID/PID/PID/..../ID -> fullIdPath = [{PID},{PID},{PID},....,{ID}]
    //fullDepth = PID:PID:PID:...:ID
    private String getFullDepth(String[] fullIdPath) {
        String fullDepth = "";
        // parent가 있다면 만들어주는 작업
        if (fullIdPath.length > 1) {
            for (String codeId : fullIdPath) {
                fullDepth += (codeId + ":");
            }
        } else if (fullIdPath.length == 1) {
            fullDepth = fullIdPath[0]+":";
        }
        return fullDepth;
    }
}
