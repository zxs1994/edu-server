package cn.dh.oa.module.oa.enums.correction;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会长纠错类型
 */
@Getter
@AllArgsConstructor
public enum OaCorrectionTypeEnum {

    OBJECTION(1, "异议纠错"),
    COUNCIL(2, "理事会决议");

    private final Integer type;
    private final String name;

    public static boolean isCouncil(Integer type) {
        return COUNCIL.type.equals(type);
    }

}
