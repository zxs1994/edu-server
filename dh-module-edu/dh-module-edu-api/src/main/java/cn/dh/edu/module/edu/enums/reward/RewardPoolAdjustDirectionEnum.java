package cn.dh.edu.module.edu.enums.reward;

import cn.dh.edu.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 奖金池调账方向
 */
@Getter
@AllArgsConstructor
public enum RewardPoolAdjustDirectionEnum implements ArrayValuable<String> {

    UP("UP", "充值"),
    DOWN("DOWN", "调减");

    public static final String[] ARRAYS = Arrays.stream(values()).map(RewardPoolAdjustDirectionEnum::getDirection).toArray(String[]::new);

    private final String direction;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
