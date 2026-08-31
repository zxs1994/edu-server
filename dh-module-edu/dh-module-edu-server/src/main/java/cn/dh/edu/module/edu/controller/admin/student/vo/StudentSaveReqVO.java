package cn.dh.edu.module.edu.controller.admin.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 学生档案保存 Request VO")
@Data
public class StudentSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "性别不能为空")
    private Integer sex;

    @Schema(description = "出生日期", example = "2000-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate birthday;

    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20240001")
    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @Schema(description = "用户账号（新增时系统自动生成，如 S0001）", example = "S0001")
    private String username;

    @Schema(description = "入学年份", example = "2024")
    private Integer enrollYear;

    @Schema(description = "所属院系", example = "航海学院")
    private String college;

    @Schema(description = "专业", example = "航海技术")
    private String major;

    @Schema(description = "班级", example = "航技2401")
    private String className;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "在校状态（1:在读 2:毕业 3:休学）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "在校状态不能为空")
    private Integer schoolStatus;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
