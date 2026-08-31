package cn.dh.edu.module.edu.controller.admin.teacher.vo;

import cn.dh.edu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 教培档案分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TeacherPageReqVO extends PageParam {

    @Schema(description = "用户账号", example = "teacher01")
    private String username;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "职称/职级", example = "1")
    private String title;

    @Schema(description = "报酬/奖励标准", example = "A")
    private String rewardStandard;

    @Schema(description = "联系方式", example = "13800138000")
    private String mobile;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
