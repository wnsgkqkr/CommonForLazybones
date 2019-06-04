package com.cfl.controller;

import com.cfl.service.ObjectService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ObjectController {
    @Autowired
    private ObjectService objectService;

    @PostMapping(value="/object/authority")
    public JSONObject insertObjectAuthority(@RequestBody JSONObject requestObject){
        return objectService.insertObjectAuthority(requestObject);
    }
    @PutMapping(value="/object/authority")
    public JSONObject updateObjectAuthority(@RequestBody JSONObject requestObject){
        return objectService.updateObjectAuthority(requestObject);
    }
    @DeleteMapping(value="/object/authority")
    public JSONObject deleteObjectAuthority(@RequestBody JSONObject requestObject){
        return objectService.deleteObjectAuthority(requestObject);
    }
    @GetMapping(value="/object/authority")
    public JSONObject getObjectAuthority(@RequestBody JSONObject requestObject){
        return objectService.getObjectAuthority(requestObject);
    }

    @PostMapping(value="/object")
    public JSONObject insertObject(@RequestBody JSONObject requestObject){
        return objectService.insertObject(requestObject);
    }
    @PutMapping(value="/object")
    public JSONObject updateObject(@RequestBody JSONObject requestObject){
        return objectService.updateObject(requestObject);
    }
    @DeleteMapping(value="/object")
    public JSONObject deleteObject(@RequestBody JSONObject requestObject){
        return objectService.deleteObject(requestObject);
    }
    @GetMapping(value="/object")
    public JSONObject getObject(@RequestBody JSONObject requestObject){
        return objectService.getObject(requestObject);
    }
}
