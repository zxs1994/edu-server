package cn.dh.edu.module.edu.controller.admin.teacher.vo;

import cn.dh.edu.framework.excel.core.annotations.DictFormat;
import cn.dh.edu.framework.excel.core.convert.DictConvert;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 教培档案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TeacherRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "teacher01")
    @ExcelProperty("用户账号")
    private String username;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("姓名")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "性别", converter = DictConvert.class)
    @DictFormat("system_user_sex")
    private Integer sex;

    @Schema(description = "职称/职级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "职称/职级", converter = DictConvert.class)
    @DictFormat("edu_teacher_title")
    private String title;

    @Schema(description = "联系方式", example = "13800138000")
    @ExcelProperty("联系方式")
    private String mobile;

    @Schema(description = "报酬/奖励标准（金额；选项来自字典 edu_teacher_reward）", example = "500.00")
    @ExcelProperty("报酬/奖励标准")
    private BigDecimal rewardStandard;

    @Schema(description = "关联用户ID", example = "1")
    private Long userId;

    @Schema(description = "是否已生成用户", example = "false")
    private Boolean userGenerated;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
