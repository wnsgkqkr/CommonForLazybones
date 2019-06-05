package com.cfl.mapper;

import com.cfl.domain.AllowedServer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AllowedServerMapper {
    AllowedServer selectAllowedServerByIpv4(String ipv4Address);
    AllowedServer insertAllowedServer(AllowedServer allowedServer);
    AllowedServer updateAllowedServer(AllowedServer allowedServer, String originalIp);
    AllowedServer deleteAllowedServer(AllowedServer allowedServer);
    AllowedServer selectAllowedServer(AllowedServer allowedServer);

    List<AllowedServer> selectRegExpServerList();
}
