package cn.dh.oa.module.oa.api.correction;

import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionRevokeNodeDTO;

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

}
