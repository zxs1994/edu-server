package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;

import java.util.Set;

/**
 * 纠错原单据加载与流程更新
 */
public interface BillCorrectionSourceService {

  Set<String> SUPPORTED_BILL_TYPES = OaBillTypeEnum.presidentCorrectionBillTypeKeys();

  BillCorrectionSourceDTO loadRequired(String billType, Long billId);

  boolean exists(String billType, Long billId);

  void updateForReApproval(BillCorrectionSourceDTO source, String newProcessInstanceId);

  void applyReApprovalCompleted(String billType, Long billId, String processInstanceId, Integer status);

  void updateForCouncilOverride(BillCorrectionSourceDTO source);

  /** 理事会决议推翻：原单据流程状态置为不通过 */
  void applyCouncilReject(String billType, Long billId);

}
