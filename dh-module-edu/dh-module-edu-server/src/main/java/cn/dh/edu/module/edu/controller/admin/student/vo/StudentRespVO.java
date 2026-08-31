package cn.dh.edu.module.edu.controller.admin.student.vo;

import cn.dh.edu.framework.excel.core.annotations.DictFormat;
import cn.dh.edu.framework.excel.core.convert.DictConvert;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 学生档案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StudentRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("姓名")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "性别", converter = DictConvert.class)
    @DictFormat("system_user_sex")
    private Integer sex;

    @Schema(description = "出生日期", example = "2000-01-01")
    @ExcelProperty("出生日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate birthday;

    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20240001")
    @ExcelProperty("学号")
    private String studentNo;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20240001")
    @ExcelProperty("用户账号")
    private String username;

    @Schema(description = "入学年份", example = "2024")
    @ExcelProperty("入学年份")
    private Integer enrollYear;

    @Schema(description = "所属院系", example = "航海学院")
    @ExcelProperty("所属院系")
    private String college;

    @Schema(description = "专业", example = "航海技术")
    @ExcelProperty("专业")
    private String major;

    @Schema(description = "班级", example = "航技2401")
    @ExcelProperty("班级")
    private String className;

    @Schema(description = "手机号", example = "13800138000")
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "在校状态（1:在读 2:毕业 3:休学）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "在校状态", converter = DictConvert.class)
    @DictFormat("edu_student_status")
    private Integer schoolStatus;

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
