package cn.dh.oa.module.oa.api.correction;

import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionRevokeNodeDTO;

import java.util.Set;
/**
 * 会长纠错 API（供 BPM 审批详情增强、流程完成回调）
 */
public interface OaPresidentCorrectionApi {

    /**
     * 根据原流程实例 ID 获取虚拟撤销节点信息
     */
    OaCorrectionRevokeNodeDTO getRevokeNode(String sourceProcessInstanceId);

    /**
     * 重审流程结束后的纠错收尾
     */
    void onReApprovalProcessCompleted(String processDefinitionKey, String businessKey,
                                      String processInstanceId, Integer status);

    /**
     * 业务单据是否处于冻结中
     */
    boolean isBillFrozen(String billType, Long billId);

    /**
     * 展示层是否叠加「会长异议/纠错」（冻结且纠错进行中）
     */
    boolean shouldDisplayCorrectionOverlay(String billType, Long billId);

    /**
     * 列表状态列：是否仅展示「会长异议/纠错」（未重新发起 / 理事会推翻）
     */
    boolean isAwaitingResubmitAfterCorrection(String billType, Long billId);

    /**
     * 纠错冻结中单据由申请人重新提交审批后，关联新流程实例
     */
    void onSourceBillResubmitted(String billType, Long billId, String processInstanceId);

    /**
     * 冻结中单据是否允许同步该流程实例的状态（仅纠错重提后的新流程）
     */
    boolean shouldSyncFrozenBillProcessStatus(String billType, Long billId, String processInstanceId);

    /**
     * 会长纠错档案引用的原流程实例 ID（重新发起时不得从历史库删除）
     */
    Set<String> listProtectedSourceProcessInstanceIds(String billType, Long billId);

    /**
     * 原流程已被纠错重提的新流程替代时，列表不再展示该原流程实例
     */
    boolean shouldHideCorrectedSourceProcessInstance(String billType, Long billId, String processInstanceId);

}
