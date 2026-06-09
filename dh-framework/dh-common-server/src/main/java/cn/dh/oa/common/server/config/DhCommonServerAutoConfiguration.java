package cn.dh.oa.common.server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 通用服务模块的自动配置类
 *
 * @author 鼎衡
 */
@AutoConfiguration
@ComponentScan({
    "cn.dh.oa.common.server.attachment"
})
@MapperScan({
    "cn.dh.oa.common.server.attachment.dal.mysql"
})
public class DhCommonServerAutoConfiguration {

}
