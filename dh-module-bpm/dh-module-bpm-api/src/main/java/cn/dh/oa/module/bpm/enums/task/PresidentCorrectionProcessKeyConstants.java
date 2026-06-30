package cn.dh.oa.module.bpm.enums.task;

import java.util.List;

/**
 * 会长纠错可检索/纠错的流程定义 Key
 */
public final class PresidentCorrectionProcessKeyConstants {

    private PresidentCorrectionProcessKeyConstants() {
    }

    public static final List<String> CORRECTABLE_PROCESS_KEYS = List.of(
            "oa_contract_bill",
            "oa_expense_reimburse_bill",
            "oa_seal_apply_bill",
            "oa_project_initiation_bill",
            "oa_document_dispatch_bill"
    );

}
