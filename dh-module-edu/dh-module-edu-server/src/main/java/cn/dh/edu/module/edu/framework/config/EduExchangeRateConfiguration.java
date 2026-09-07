package cn.dh.edu.module.edu.framework.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EduExchangeRateProperties.class)
public class EduExchangeRateConfiguration {
}
