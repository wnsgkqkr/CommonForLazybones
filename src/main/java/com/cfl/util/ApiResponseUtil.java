package com.cfl.util;

import com.cfl.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

@Slf4j
public class ApiResponseUtil {
    public static final String MESSAGE_SUCCESS = "SUCCESS";
    public static final String MESSAGE_FAILURE = "FAILURE";

    public static ApiResponse getSuccessApiResponse(Object successResult){
        return new ApiResponse(true, HttpStatus.SC_OK, MESSAGE_SUCCESS, successResult);
    }
    public static ApiResponse getFailureApiResponse(){
        return new ApiResponse(false, HttpStatus.SC_INTERNAL_SERVER_ERROR, MESSAGE_FAILURE);
    }
}
