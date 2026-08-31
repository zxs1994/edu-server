package cn.dh.edu.framework.idempotent.config;

import cn.dh.edu.framework.idempotent.core.aop.IdempotentAspect;
import cn.dh.edu.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import cn.dh.edu.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import cn.dh.edu.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import cn.dh.edu.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import cn.dh.edu.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import cn.dh.edu.framework.redis.config.DhRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = DhRedisAutoConfiguration.class)
public class DhIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
