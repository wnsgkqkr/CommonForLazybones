package com.cfl.util;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
public class CommonUtil {
    public static final String MESSAGE_SUCCESS = "SUCCESS";
    public static final String MESSAGE_FAILURE = "FAILURE";

    //set response API = isSuccess(Boolean), resultCode(int), resultMessage(String)
    public static ApiResponse getSuccessApiResponse(Object successResult){
        return new ApiResponse(true, HttpStatus.SC_OK, MESSAGE_SUCCESS, successResult);
    }
    public static ApiResponse getFailureApiResponse(){
        return new ApiResponse(false, HttpStatus.SC_INTERNAL_SERVER_ERROR, MESSAGE_FAILURE);
    }
}
