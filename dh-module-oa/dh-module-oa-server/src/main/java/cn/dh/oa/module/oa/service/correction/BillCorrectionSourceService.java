package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;

import java.util.Set;

/**
 * 纠错原单据加载与流程更新
 */
public interface BillCorrectionSourceService {

  Set<String> SUPPORTED_BILL_TYPES = Set.of(
          "oa_contract_bill",
          "oa_expense_reimburse_bill",
          "oa_seal_apply_bill",
          "oa_project_initiation_bill",
          "oa_document_dispatch_bill");

  BillCorrectionSourceDTO loadRequired(String billType, Long billId);

  void updateForReApproval(BillCorrectionSourceDTO source, String newProcessInstanceId);

  void updateForCouncilOverride(BillCorrectionSourceDTO source);

}
