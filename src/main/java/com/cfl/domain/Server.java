package com.cfl.domain;

import com.cfl.util.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Server {
    private String serverIp;
    private String serverName;
    private String serviceName;
    private String portNumber;

    public Server() { }

    public Server(String serviceName) {
        this.serviceName = serviceName;
    }

    public Server(String serviceName, String serverIp) {
        this(serviceName);
        this.serverIp = serverIp;
    }
}
