package cn.dh.oa.module.oa.enums.correction;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务单据纠错状态（oa_bill_correction_state）
 */
@Getter
@AllArgsConstructor
public enum OaBillCorrectionStatusEnum {

    NONE(0, "无"),
    IN_PROGRESS(1, "纠错中"),
    COMPLETED(2, "已完成"),
    COUNCIL_OVERRIDE(3, "理事会推翻");

    private final Integer status;
    private final String name;

    public static boolean isActive(Integer status) {
        return IN_PROGRESS.status.equals(status);
    }

}
