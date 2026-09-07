package cn.dh.edu.module.edu.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 专项活动汇率配置
 */
@Data
@ConfigurationProperties(prefix = "dh.edu.exchange-rate")
public class EduExchangeRateProperties {

    /**
     * 是否启用实时汇率 API（关闭后仅支持人民币，或需在 static-rates 中显式配置离线汇率）
     */
    private Boolean enabled = true;

    /**
     * 汇率 API 地址，{from} 为源币种代码
     */
    private String apiUrl = "https://api.frankfurter.dev/v1/latest?from={from}&to=CNY";

    /**
     * 请求超时（毫秒）
     */
    private Integer timeoutMs = 5000;

    /**
     * 本地缓存有效期
     */
    private Duration cacheTtl = Duration.ofHours(1);

    /**
     * 离线静态汇率（1 单位外币 = X 人民币），仅在 enabled=false 时生效，需运维显式配置
     */
    private Map<String, BigDecimal> staticRates = new HashMap<>();

}
