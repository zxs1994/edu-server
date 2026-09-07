package cn.dh.edu.module.edu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 专项活动周期类型
 *
 * @author 鼎衡
 */
@Getter
@AllArgsConstructor
public enum ActivityCycleTypeEnum {

    ONCE("ONCE", "单次");

    private final String type;
    private final String name;

    public static boolean isOnce(String cycleType) {
        return ONCE.type.equals(cycleType);
    }

}
