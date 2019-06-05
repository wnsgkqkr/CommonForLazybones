package com.cfl.cache;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.User;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    //Object - Authority cache
    public static Map<String, Map<String, Map<String, CflObject>>> objectAuthorityCache = new HashMap<>();
    //Authority - User Cache
    public static Map<String,Map<String,Map<String, Authority>>> authorityUserCache = new HashMap<>();
    //User - Authority Cache
    public static Map<String,Map<String,Map<String, User>>> userAuthorityCache = new HashMap<>();
}
