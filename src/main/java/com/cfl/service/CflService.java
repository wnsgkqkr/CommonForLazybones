package com.cfl.service;


import com.cfl.domain.ApiRequest;

public interface CflService<T> {
    T createData(ApiRequest request);
    T modifyData(ApiRequest request);
    T removeData(ApiRequest request);
    T getData(ApiRequest request);
}