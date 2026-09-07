package cn.dh.edu.module.edu.enums.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 专项活动费用币种（字典 edu_fee_currency）
 */
@Getter
@AllArgsConstructor
public enum EduFeeCurrencyEnum {

    CNY("CNY", "人民币"),
    USD("USD", "美元"),
    EUR("EUR", "欧元"),
    HKD("HKD", "港币");

    private final String code;
    private final String name;

    public static EduFeeCurrencyEnum getByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String normalized = code.trim().toUpperCase();
        for (EduFeeCurrencyEnum value : values()) {
            if (value.code.equals(normalized)) {
                return value;
            }
        }
        return null;
    }

}
