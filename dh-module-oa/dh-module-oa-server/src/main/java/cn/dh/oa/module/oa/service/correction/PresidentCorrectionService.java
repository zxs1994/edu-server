package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.oa.controller.admin.correction.vo.PresidentCorrectionInitiateReqVO;
import cn.dh.oa.module.oa.controller.admin.correction.vo.BillCorrectionHistoryRespVO;
import jakarta.validation.Valid;

/**
 * 会长纠错编排 Service
 */
public interface PresidentCorrectionService {

    Long initiate(Long presidentUserId, @Valid PresidentCorrectionInitiateReqVO reqVO);

    /**
     * 业务单据详情页：查询会长纠错历史（含原流程实例 ID，用于展示原审批记录）
     */
    BillCorrectionHistoryRespVO getBillHistory(String sourceBillType, Long sourceBillId);

}
