package com.cfl.service;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface CflService<T> {
    T createData(JSONObject jsonObject);
    T modifyData(JSONObject jsonObject);
    T removeData(JSONObject jsonObject);
    T getData(JSONObject jsonObject);
}