package cn.dh.edu.module.edu.controller.admin.student.vo;

import cn.dh.edu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 学生档案分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StudentPageReqVO extends PageParam {

    @Schema(description = "学号", example = "20240001")
    private String studentNo;

    @Schema(description = "用户账号", example = "20240001")
    private String username;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "所属院系", example = "航海学院")
    private String college;

    @Schema(description = "专业", example = "航海技术")
    private String major;

    @Schema(description = "班级", example = "航技2401")
    private String className;

    @Schema(description = "在校状态（1:在读 2:毕业 3:休学）", example = "1")
    private Integer schoolStatus;

    @Schema(description = "入学年份", example = "2024")
    private Integer enrollYear;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
