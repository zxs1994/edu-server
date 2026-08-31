package cn.dh.edu.common.server.config;

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
    "cn.dh.edu.common.server.attachment",
    // 单据号生成（不在 module/server 扫描路径内，需显式注册）
    "cn.dh.edu.framework.common.util.bill"
})
@MapperScan({
    "cn.dh.edu.common.server.attachment.dal.mysql"
})
public class DhCommonServerAutoConfiguration {

}
