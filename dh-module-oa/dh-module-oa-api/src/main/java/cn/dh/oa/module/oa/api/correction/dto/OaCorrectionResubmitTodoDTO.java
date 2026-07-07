package cn.dh.oa.module.oa.api.correction.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会长纠错待重提 - 待办投影（供 BPM todo-page 合并展示）
 */
@Data
public class OaCorrectionResubmitTodoDTO {

    private Long correctionBillId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillCode;

    private String sourceBillTitle;

    private String sourceProcessInstanceId;

    private String correctionReason;

    private LocalDateTime revokeTime;

    private Long creatorUserId;

    private String creatorName;

    private Long companyId;

    private String companyName;

    private Long deptId;

    private String deptName;

}
