package cn.dh.oa.module.oa.service.correction.freeze;

/**
 * 业务单据冻结/解冻处理器
 */
public interface BillFreezeHandler {

    String getBillType();

    void freeze(Long billId);

    void unfreeze(Long billId);

    /**
     * 理事会决议直接推翻原审批结果
     */
    default void applyCouncilOverride(Long billId, String correctionResult) {
        // 默认仅记录日志，子类按需覆写业务终态
    }

}
