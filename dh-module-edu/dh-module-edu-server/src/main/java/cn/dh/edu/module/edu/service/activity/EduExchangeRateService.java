package cn.dh.edu.module.edu.service.activity;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 费用币种折算人民币（实时汇率 + 缓存）
 */
public interface EduExchangeRateService {

    /**
     * 获取 1 单位外币对应的人民币金额
     */
    BigDecimal getCnyRate(String currencyCode);

    /**
     * 将金额折算为人民币（保留 2 位小数）
     */
    BigDecimal toCny(BigDecimal amount, String currencyCode);

    /**
     * 预加载汇率，减少校验时多次请求
     */
    void prefetchCnyRates(Collection<String> currencyCodes);

}
