package com.cfl.mapper;

import com.cfl.domain.Server;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServerMapper {
    void insertAllowedServer(Server server);
    void updateAllowedServer(String serviceName, String tenantId, String serverIp, @Param("server") Server server);
    void deleteAllowedServer(Server server);
    Server selectAllowedServer(Server server);

    void insertProvideServer(Server server);
    Server selectProvideServer(Server server);
    List<String> selectAllProvideServerIp();

    List<Server> selectRegExpServerList(String serviceName, String tenantId);
}
