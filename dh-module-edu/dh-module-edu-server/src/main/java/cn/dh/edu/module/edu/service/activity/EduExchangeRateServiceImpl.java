package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.util.json.JsonUtils;
import cn.dh.edu.module.edu.enums.activity.EduFeeCurrencyEnum;
import cn.dh.edu.module.edu.framework.config.EduExchangeRateProperties;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.ACTIVITY_EXCHANGE_RATE_FETCH_FAILED;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.ACTIVITY_FEE_CURRENCY_UNSUPPORTED;

@Slf4j
@Service
public class EduExchangeRateServiceImpl implements EduExchangeRateService {

    private static final String TARGET_CURRENCY = "CNY";

    @Resource
    private EduExchangeRateProperties properties;

    private final Map<String, CachedRate> rateCache = new ConcurrentHashMap<>();

    @Override
    public BigDecimal getCnyRate(String currencyCode) {
        EduFeeCurrencyEnum currencyEnum = resolveCurrency(currencyCode);
        if (currencyEnum == EduFeeCurrencyEnum.CNY) {
            return BigDecimal.ONE;
        }

        String normalizedCode = currencyEnum.getCode();
        CachedRate cached = rateCache.get(normalizedCode);
        if (cached != null && !cached.isExpired()) {
            return cached.rate();
        }

        BigDecimal rate = fetchCnyRate(normalizedCode);
        rateCache.put(normalizedCode, CachedRate.of(rate, properties.getCacheTtl()));
        return rate;
    }

    @Override
    public BigDecimal toCny(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(getCnyRate(currencyCode)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void prefetchCnyRates(Collection<String> currencyCodes) {
        if (currencyCodes == null || currencyCodes.isEmpty()) {
            return;
        }
        currencyCodes.stream()
                .filter(StringUtils::isNotBlank)
                .distinct()
                .forEach(this::getCnyRate);
    }

    private BigDecimal fetchCnyRate(String currencyCode) {
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            return resolveConfiguredStaticRate(currencyCode, "实时汇率已关闭");
        }
        try {
            String url = properties.getApiUrl().replace("{from}", currencyCode);
            HttpResponse response = HttpRequest.get(url)
                    .timeout(properties.getTimeoutMs())
                    .setFollowRedirects(true)
                    .execute();
            if (!response.isOk()) {
                throw new IllegalStateException("HTTP " + response.getStatus());
            }
            BigDecimal rate = parseCnyRate(response.body());
            if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("汇率响应无效");
            }
            log.info("[fetchCnyRate] 获取实时汇率成功，{} -> CNY = {}", currencyCode, rate);
            return rate;
        } catch (Exception ex) {
            log.error("[fetchCnyRate] 获取实时汇率失败，{} -> CNY，原因：{}", currencyCode, ex.getMessage());
            throw exception(ACTIVITY_EXCHANGE_RATE_FETCH_FAILED, currencyCode);
        }
    }

    /**
     * 仅 enabled=false 时使用配置中的 static-rates，避免 API 失败时静默套用过期汇率
     */
    private BigDecimal resolveConfiguredStaticRate(String currencyCode, String reason) {
        BigDecimal rate = properties.getStaticRates().get(currencyCode);
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("[fetchCnyRate] {}，且未配置 static-rates.{}，{} -> CNY 无法折算",
                    reason, currencyCode, currencyCode);
            throw exception(ACTIVITY_EXCHANGE_RATE_FETCH_FAILED, currencyCode);
        }
        log.info("[fetchCnyRate] {}，使用配置 static-rates，{} -> CNY = {}", reason, currencyCode, rate);
        return rate;
    }

    private BigDecimal parseCnyRate(String body) {
        JsonNode root = JsonUtils.parseTree(body);
        JsonNode rates = root.path("rates");
        if (rates.isMissingNode() || !rates.has(TARGET_CURRENCY)) {
            return null;
        }
        return rates.get(TARGET_CURRENCY).decimalValue();
    }

    private EduFeeCurrencyEnum resolveCurrency(String currencyCode) {
        EduFeeCurrencyEnum currencyEnum = EduFeeCurrencyEnum.getByCode(currencyCode);
        if (currencyEnum == null) {
            throw exception(ACTIVITY_FEE_CURRENCY_UNSUPPORTED, currencyCode);
        }
        return currencyEnum;
    }

    private record CachedRate(BigDecimal rate, Instant expireAt) {

        static CachedRate of(BigDecimal rate, java.time.Duration ttl) {
            return new CachedRate(rate, Instant.now().plus(ttl));
        }

        boolean isExpired() {
            return Instant.now().isAfter(expireAt);
        }
    }

}
