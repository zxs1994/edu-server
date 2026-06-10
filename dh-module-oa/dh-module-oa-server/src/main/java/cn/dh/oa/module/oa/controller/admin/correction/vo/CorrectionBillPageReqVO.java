package cn.dh.oa.module.oa.controller.admin.correction.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 纠错申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CorrectionBillPageReqVO extends PageParam {

    @Schema(description = "单据编号", example = "JC202412010001")
    private String billCode;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "原单据类型", example = "103")
    private String sourceBillType;

    @Schema(description = "冻结状态 0未冻结 1已冻结 2已解冻", example = "0")
    private Integer freezeStatus;

    @Schema(description = "纠错状态 0待处理 1重审中 2已完成 3已撤销", example = "0")
    private Integer correctionStatus;

    @Schema(description = "创建人（用户ID）", example = "1")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
