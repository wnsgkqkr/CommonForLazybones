package com.cfl.mapper;

import com.cfl.domain.Server;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServerMapper {
    void insertAllowedServer(@Param("server") Server server);
    void updateAllowedServer(@Param("server") Server server);
    void deleteAllowedServer(@Param("server") Server server);
    Server selectAllowedServer( @Param("server")Server server);

    void insertProvideServer(@Param("server") Server server);
    Server selectProvideServerByServerIp(@Param("server") Server server);
    List<Server> selectProvideServerByServiceName(String serviceName);

    List<Server> selectRegExpServerList(@Param("serviceName") String serviceName, @Param("tenantId") String tenantId);
}
