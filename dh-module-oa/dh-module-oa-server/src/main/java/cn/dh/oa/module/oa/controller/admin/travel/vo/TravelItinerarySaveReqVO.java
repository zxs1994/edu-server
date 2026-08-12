package cn.dh.oa.module.oa.controller.admin.travel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Schema(description = "管理后台 - 差旅行程明细新增/修改 Request VO")
@Data
public class TravelItinerarySaveReqVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "差旅申请单ID")
    private Long billId;

    @Schema(description = "出发城市")
    private String departureCity;

    @Schema(description = "到达城市")
    private String destinationCity;

    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;

    @Schema(description = "交通方式：1火车 2飞机 3自驾 4公务用车 5其他")
    private Integer transportType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer sortOrder;

}
