package cn.dh.oa.framework.signature.config;

import cn.dh.oa.framework.redis.config.DhRedisAutoConfiguration;
import cn.dh.oa.framework.signature.core.aop.ApiSignatureAspect;
import cn.dh.oa.framework.signature.core.redis.ApiSignatureRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名的自动配置类
 *
 */
@AutoConfiguration(after = DhRedisAutoConfiguration.class)
public class DhApiSignatureAutoConfiguration {

    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
        return new ApiSignatureAspect(signatureRedisDAO);
    }

    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }

}
