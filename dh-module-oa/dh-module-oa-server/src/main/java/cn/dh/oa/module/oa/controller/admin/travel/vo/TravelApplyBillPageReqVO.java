package cn.dh.oa.module.oa.controller.admin.travel.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 差旅申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TravelApplyBillPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "目的地")
    private String destination;

    @Schema(description = "交通方式：1火车 2飞机 3自驾 4公务用车 5其他")
    private Integer transportType;

    @Schema(description = "住宿类型：1酒店 2招待所 3其他")
    private Integer accommodationType;

    @Schema(description = "是否出国：0否 1是")
    private Integer isOverseas;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
