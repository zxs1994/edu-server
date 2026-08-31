package cn.dh.edu.module.edu.enums;

import cn.dh.edu.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学生在校状态枚举
 *
 * @author 鼎衡
 */
@Getter
@AllArgsConstructor
public enum StudentSchoolStatusEnum implements ArrayValuable<Integer> {

    ENROLLED(1, "在读"),
    GRADUATED(2, "毕业"),
    SUSPENDED(3, "休学");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(StudentSchoolStatusEnum::getStatus)
            .toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isEnrolled(Integer status) {
        return ObjUtil.equal(ENROLLED.status, status);
    }

}
