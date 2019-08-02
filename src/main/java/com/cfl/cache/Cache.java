package com.cfl.cache;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.Code;
import com.cfl.domain.User;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    public static Map<String, Map<String, Map<String, CflObject>>> objectAuthorityCache = new HashMap<>();
    public static Map<String,Map<String,Map<String, Authority>>> authorityUserCache = new HashMap<>();
    public static Map<String,Map<String,Map<String, User>>> userAuthorityCache = new HashMap<>();
    public static Map<String,Map<String,Map<String, Code>>> codeCache = new HashMap<>();
    public static Map<String,Map<String,Map<String, Code>>> usingCodeCache = new HashMap<>();
}
