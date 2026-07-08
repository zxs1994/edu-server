package cn.dh.oa.module.oa.enums;

import cn.dh.oa.framework.common.enums.BillTypeEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 单据类型枚举
 *
 * @author 鼎衡
 */
public enum OaBillTypeEnum implements BillTypeEnum {
    /**
     * 用车申请单
     */
    OA_CAR_APPLY_BILL("101", "用车申请单","oa_car_apply_bill"),
    
    /**
     * 还车申请单
     */
    OA_CAR_RETURN_BILL("102", "还车申请单","oa_car_return_bill"),
    
    /**
     * 用印申请单
     */
    OA_SEAL_APPLY_BILL("103", "用印申请单","oa_seal_apply_bill"),
    
    /**
     * 会议室预定申请单
     */
    OA_MEETING_ROOM_BOOKING("104", "会议室预定申请单","oa_meeting_room_booking"),

    /**
     * 合同审批单
     */
    OA_CONTRACT_BILL("105", "合同审批单", "oa_contract_bill"),

    /**
     * 公文发文单
     */
    OA_DOCUMENT_DISPATCH_BILL("106", "公文发文单", "oa_document_dispatch_bill"),

    /**
     * 费用报销单
     */
    OA_EXPENSE_REIMBURSE_BILL("107", "费用报销单", "oa_expense_reimburse_bill"),

    /**
     * 项目立项单
     */
    OA_PROJECT_INITIATION_BILL("108", "项目立项单", "oa_project_initiation_bill"),

    /**
     * 差旅申请单
     */
    OA_TRAVEL_APPLY_BILL("109", "差旅申请单", "oa_travel_apply_bill"),

    /**
     * 出境差旅申请单（与差旅申请单共用数据表，流程定义独立）
     */
    OA_OVERSEAS_TRAVEL_APPLY_BILL("113", "出境差旅申请单", "oa_travel_apply_bill_copy"),

    /**
     * 收文办理单
     */
    OA_INCOMING_DOCUMENT_BILL("110", "收文办理单", "oa_incoming_document_bill"),

    /**
     * 纠错申请单
     */
    OA_CORRECTION_BILL("111", "纠错申请单", "oa_correction_bill"),

    /**
     * 日常报销单
     */
    OA_DAILY_EXPENSE_BILL("112", "日常报销单", "oa_daily_expense_bill"),

    /**
     * 费用支出申请
     */
    OA_EXPENSE_PAYMENT_BILL("114", "费用支出申请", "oa_expense_payment_bill");

    /**
     * 单据类型代码
     */
    private final String code;

    /**
     * 单据类型名称
     */
    private final String name;

    /**
     * 流程定义key
     */
    private final String processDefinitionKey;

    /**
     * 构造方法
     *
     * @param code 单据类型代码
     * @param name 单据类型名称
     */
    OaBillTypeEnum(String code, String name, String processDefinitionKey) {
        this.code = code;
        this.name = name;
        this.processDefinitionKey = processDefinitionKey;
    }

    @Override
    public String getTypeCode() {
        return code;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public static OaBillTypeEnum getByProcessDefinitionKey(String processDefinitionKey) {
        if (processDefinitionKey == null) {
            return null;
        }
        for (OaBillTypeEnum value : values()) {
            if (processDefinitionKey.equals(value.processDefinitionKey)) {
                return value;
            }
        }
        return null;
    }

    /** 会长纠错支持的单据类型（协同办公全部流程单据，不含纠错单自身） */
    public static Set<String> presidentCorrectionBillTypeKeys() {
        return Arrays.stream(values())
                .filter(type -> type != OA_CORRECTION_BILL)
                .map(OaBillTypeEnum::getProcessDefinitionKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static boolean isPresidentCorrectionSupported(String billType) {
        return billType != null && presidentCorrectionBillTypeKeys().contains(billType);
    }

}
