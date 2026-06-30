package cn.dh.oa.module.oa.controller.admin.correction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "会长发起纠错 Request VO")
@Data
public class PresidentCorrectionInitiateReqVO {

    @Schema(description = "原单据类型（流程定义Key）", requiredMode = Schema.RequiredMode.REQUIRED, example = "oa_contract_bill")
    @NotEmpty(message = "原单据类型不能为空")
    private String sourceBillType;

    @Schema(description = "原单据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "原单据ID不能为空")
    private Long sourceBillId;

    @Schema(description = "纠错类型（1异议纠错 2理事会决议）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "纠错类型不能为空")
    private Integer correctionType;

    @Schema(description = "撤销/纠错理由", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "撤销/纠错理由不能为空")
    private String correctionReason;

    @Schema(description = "是否理事会决议 0否 1是")
    private Integer councilDecision;

    @Schema(description = "理事会决议文件")
    private String councilDecisionFile;

    @Schema(description = "纠错处理结果（理事会决议必填）")
    private String correctionResult;

}
