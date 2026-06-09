package cn.dh.oa.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 *
 * @author 鼎衡
 */
@ConfigurationProperties(prefix = "dh.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "dh.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
