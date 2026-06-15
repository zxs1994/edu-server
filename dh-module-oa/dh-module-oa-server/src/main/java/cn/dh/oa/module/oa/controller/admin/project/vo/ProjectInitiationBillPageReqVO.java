package cn.dh.oa.module.oa.controller.admin.project.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 项目立项单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectInitiationBillPageReqVO extends PageParam {

    @Schema(description = "项目编号")
    private String billCode;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目类型：1研发型 2交付实施型 3工程建造型")
    private Integer projectType;

    @Schema(description = "立项状态")
    private Integer processStatus;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
