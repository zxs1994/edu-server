package cn.dh.oa.module.oa.controller.admin.correction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "业务单据 - 会长纠错历史条目")
@Data
public class BillCorrectionHistoryItemVO {

    @Schema(description = "纠错单 ID")
    private Long correctionId;

    @Schema(description = "审批版本号")
    private Integer approvalVersion;

    @Schema(description = "纠错类型：1异议纠错 2理事会决议")
    private Integer correctionType;

    @Schema(description = "纠错原因")
    private String correctionReason;

    @Schema(description = "撤销人姓名")
    private String revokeUserName;

    @Schema(description = "撤销时间")
    private LocalDateTime revokeTime;

    @Schema(description = "原流程实例 ID")
    private String sourceProcessInstanceId;

    @Schema(description = "重审流程实例 ID")
    private String newProcessInstanceId;

    @Schema(description = "纠错状态")
    private Integer correctionStatus;

    @Schema(description = "纠错结果说明")
    private String correctionResult;

    @Schema(description = "理事会决议附件")
    private String councilDecisionFile;

}
