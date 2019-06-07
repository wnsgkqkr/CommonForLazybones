package com.cfl.mapper;

import com.cfl.domain.AllowedServer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AllowedServerMapper {
    AllowedServer selectAllowedServerByIpv4(String ipv4Address);
    void insertAllowedServer(AllowedServer allowedServer);
    void updateAllowedServer(AllowedServer allowedServer, String originalIp);
    void deleteAllowedServer(AllowedServer allowedServer);
    AllowedServer selectAllowedServer(AllowedServer allowedServer);

    List<AllowedServer> selectRegExpServerList();
}
