package cn.dh.edu.module.edu.enums.reward;

import cn.dh.edu.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 预算配置年度预算时段模式
 */
@Getter
@AllArgsConstructor
public enum RewardBudgetPeriodModeEnum implements ArrayValuable<String> {

    QUARTER("QUARTER", "按季度"),
    MONTH("MONTH", "按月"),
    CUSTOM("CUSTOM", "自定义");

    public static final String[] ARRAYS = Arrays.stream(values())
            .map(RewardBudgetPeriodModeEnum::getMode).toArray(String[]::new);

    private final String mode;
    private final String name;

    public static RewardBudgetPeriodModeEnum of(String mode) {
        return Arrays.stream(values())
                .filter(item -> item.getMode().equals(mode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
