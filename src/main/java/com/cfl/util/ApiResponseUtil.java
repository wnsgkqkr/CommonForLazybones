package com.cfl.util;

import com.cfl.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiResponseUtil {

    private static final int CODE_SUCCESS = 1;
    private static final int CODE_DUPLICATE_MAPPING = 2;
    private static final int CODE_FAILURE = 100;
    private static final int CODE_DUPLICATE_CREATE = 101;
    private static final int CODE_MISSING_VALUE = 102;
    private static final int CODE_SUB_OBJECT_MAPPING_ERROR = 103;

    private static final String MESSAGE_SUCCESS = "SUCCESS";
    private static final String MESSAGE_DUPLICATE_MAPPING = "DUPLICATE_MAPPING";
    private static final String MESSAGE_FAILURE = "FAILURE";
    private static final String MESSAGE_DUPLICATE_CREATION = "DUPLICATE_CREATION";
    private static final String MESSAGE_MISSING_VALUE = "MISSING_VALUE";
    private static final String MESSAGE_SUB_OBJECT_MAPPING_ERROR = "SUB_OBJECT_MAPPING_ERROR";

    public static ApiResponse getSuccessApiResponse(Object result) {
        return new ApiResponse(true, CODE_SUCCESS, MESSAGE_SUCCESS, result);
    }

    public static ApiResponse getDuplicateMappingApiResponse(Object result) {
        return new ApiResponse(true, CODE_DUPLICATE_MAPPING, MESSAGE_DUPLICATE_MAPPING, result);
    }

    public static ApiResponse getFailureApiResponse() {
        return new ApiResponse(false, CODE_FAILURE, MESSAGE_FAILURE);
    }

    public static ApiResponse getDuplicateCreationApiResponse() {
        return new ApiResponse(false, CODE_DUPLICATE_CREATE, MESSAGE_DUPLICATE_CREATION);
    }

    public static ApiResponse getMissingValueApiResponse() {
        return new ApiResponse(false, CODE_MISSING_VALUE, MESSAGE_MISSING_VALUE);
    }

    public static ApiResponse getSubObjectMappingErrorApiResponse() {
        return new ApiResponse(false, CODE_SUB_OBJECT_MAPPING_ERROR, MESSAGE_SUB_OBJECT_MAPPING_ERROR);
    }
}
