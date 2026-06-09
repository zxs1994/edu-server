package cn.dh.oa.module.system.framework.rpc.config;

import cn.dh.oa.module.infra.api.config.ConfigApi;
import cn.dh.oa.module.infra.api.file.FileApi;
import cn.dh.oa.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
