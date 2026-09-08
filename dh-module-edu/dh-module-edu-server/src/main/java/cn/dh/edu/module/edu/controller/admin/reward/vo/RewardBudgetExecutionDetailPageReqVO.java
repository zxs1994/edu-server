package cn.dh.edu.module.edu.controller.admin.reward.vo;

import cn.dh.edu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 预算执行明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RewardBudgetExecutionDetailPageReqVO extends PageParam {

    @Schema(description = "预算年度", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026")
    @NotNull(message = "预算年度不能为空")
    private Integer budgetYear;

    @Schema(description = "时段ID（优先；与起止日期二选一或同时传以校验）", example = "1")
    private Long periodId;

    @Schema(description = "开始日期（未传 periodId 时必填）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期（未传 periodId 时必填）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

}
