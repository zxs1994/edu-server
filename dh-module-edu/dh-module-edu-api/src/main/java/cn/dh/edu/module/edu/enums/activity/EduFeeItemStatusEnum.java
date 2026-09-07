package cn.dh.edu.module.edu.enums.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 专项活动执行实例费用明细状态（字典 edu_fee_item_status）
 */
@Getter
@AllArgsConstructor
public enum EduFeeItemStatusEnum {

    PENDING_REQUEST("pending_request", "待付款申请"),
    SUBMITTED("submitted", "已提交付款申请"),
    CREDITED("credited", "已入账（积分）"), // 历史状态，学生侧现与教培统一走付款申请
    PAID("paid", "已付款"),
    REJECTED("rejected", "已驳回");

    private final String status;
    private final String name;

}
