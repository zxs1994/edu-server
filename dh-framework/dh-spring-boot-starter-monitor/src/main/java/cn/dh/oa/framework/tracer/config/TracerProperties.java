package cn.dh.oa.framework.tracer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizTracer配置类
 *
 */
@ConfigurationProperties("dh.tracer")
@Data
public class TracerProperties {
}
