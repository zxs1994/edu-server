package cn.dh.oa.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 http://ruoyioffice.com/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 http://ruoyioffice.com/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 http://ruoyioffice.com/quick-start/ 文章
 *
 * @author 鼎衡
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${dh.info.base-package}
@SpringBootApplication(scanBasePackages = {"${dh.info.base-package}.server", "${dh.info.base-package}.module"},
        excludeName = {
            // RPC 相关
//            "org.springframework.cloud.openfeign.FeignAutoConfiguration",
//            "cn.dh.oa.module.system.framework.rpc.config.RpcConfiguration"
        })
public class DhServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(DhServerApplication.class, args);
//        new SpringApplicationBuilder(DhServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

    }

}
