package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 奖金池更新 Request VO")
@Data
public class RewardPoolUpdateReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "池名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "专项活动奖金池")
    @NotBlank(message = "池名称不能为空")
    @Size(max = 100, message = "池名称长度不能超过100")
    private String name;

    @Schema(description = "备注", example = "备注说明")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

}
