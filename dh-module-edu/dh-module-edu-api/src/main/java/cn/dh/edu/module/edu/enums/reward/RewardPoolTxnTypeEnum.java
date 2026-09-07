package cn.dh.edu.module.edu.enums.reward;

import cn.dh.edu.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 奖金池流水类型
 */
@Getter
@AllArgsConstructor
public enum RewardPoolTxnTypeEnum implements ArrayValuable<String> {

    RECHARGE("RECHARGE", "充值"),
    ADJUST_DOWN("ADJUST_DOWN", "调减"),
    FREEZE("FREEZE", "冻结"),
    UNFREEZE("UNFREEZE", "解冻"),
    PAY("PAY", "实发");

    public static final String[] ARRAYS = Arrays.stream(values()).map(RewardPoolTxnTypeEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
