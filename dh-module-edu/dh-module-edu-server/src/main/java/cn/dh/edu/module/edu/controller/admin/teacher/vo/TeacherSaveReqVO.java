package cn.dh.edu.module.edu.controller.admin.teacher.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 教培档案保存 Request VO")
@Data
public class TeacherSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "用户账号（新增时系统自动生成，如 T0001）", example = "T0001")
    private String username;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "性别不能为空")
    private Integer sex;

    @Schema(description = "职称/职级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "职称/职级不能为空")
    private String title;

    @Schema(description = "联系方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "报酬/奖励标准（金额；选项来自字典 edu_teacher_reward）", example = "500.00")
    private BigDecimal rewardStandard;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
