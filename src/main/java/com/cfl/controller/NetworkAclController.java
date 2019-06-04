package com.cfl.controller;

import com.cfl.customexception.GetHttpStatusException;
import com.cfl.service.NetworkAclService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@Slf4j
public class NetworkAclController {
    @Autowired
    private NetworkAclService networkAclService;

    //network ACL insert, update, delete
    @RequestMapping(value="/network-acl")
    public JSONObject insertNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            networkAclService.insertNetworkAcl(requestObject);
            return successResult();
        } catch (Exception e){
            return failResult();
        }
    }
    @PutMapping(value="/network-acl")
    public JSONObject updateNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            networkAclService.updateNetworkAcl(requestObject);
            return successResult();
        } catch (Exception e){
            return failResult();
        }
    }
    @DeleteMapping(value="/network-acl")
    public JSONObject deleteNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            networkAclService.deleteNetworkAcl(requestObject);
            return successResult();
        } catch (Exception e){
            return failResult();
        }
    }

    //set response API = isSuccess(Boolean), resultCode(int), resultMessage(String)
    private JSONObject successResult(){
        JSONObject jsonObject = getHttpResponseProperty();
        jsonObject.put("isSuccess", true);

        return jsonObject;
    }
    private JSONObject failResult(){
        JSONObject jsonObject = getHttpResponseProperty();
        jsonObject.put("isSuccess", false);

        return jsonObject;
    }

    private JSONObject getHttpResponseProperty() {
        JSONObject jsonObject = new JSONObject();

        HttpClient client = HttpClientBuilder.create().build();
        try{
            HttpResponse httpResponse = client.execute(new HttpGet());

            jsonObject.put("resultCode", httpResponse.getStatusLine().getStatusCode());
            jsonObject.put("resultMessage", httpResponse.getStatusLine().getReasonPhrase());

            return jsonObject;
        } catch (IOException e){
            throw new GetHttpStatusException(e);
        }
    }
}
