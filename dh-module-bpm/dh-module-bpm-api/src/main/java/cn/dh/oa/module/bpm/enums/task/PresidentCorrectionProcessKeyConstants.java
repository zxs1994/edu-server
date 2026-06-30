package cn.dh.oa.module.bpm.enums.task;

import java.util.List;

/**
 * 会长纠错流程范围常量
 */
public final class PresidentCorrectionProcessKeyConstants {

    private PresidentCorrectionProcessKeyConstants() {
    }

    /** BPM 流程分类：协同办公 */
    public static final String OA_CATEGORY_CODE = "OA";

    /** 已完整接入纠错发起/冻结/重审的流程定义 Key */
    public static final List<String> CORRECTABLE_PROCESS_KEYS = List.of(
            "oa_contract_bill",
            "oa_expense_reimburse_bill",
            "oa_seal_apply_bill",
            "oa_project_initiation_bill",
            "oa_document_dispatch_bill"
    );

}
