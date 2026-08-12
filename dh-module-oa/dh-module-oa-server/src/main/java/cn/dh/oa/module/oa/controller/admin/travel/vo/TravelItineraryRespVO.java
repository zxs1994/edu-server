package cn.dh.oa.module.oa.controller.admin.travel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

@Schema(description = "管理后台 - 差旅行程明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TravelItineraryRespVO {

    @Schema(description = "ID")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "差旅申请单ID")
    private Long billId;

    @ExcelProperty("出发城市")
    @Schema(description = "出发城市")
    private String departureCity;

    @ExcelProperty("到达城市")
    @Schema(description = "到达城市")
    private String destinationCity;

    @ExcelProperty("开始日期")
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startDate;

    @ExcelProperty("结束日期")
    @Schema(description = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;

    @ExcelProperty("交通方式")
    @Schema(description = "交通方式：1火车 2飞机 3自驾 4公务用车 5其他")
    private Integer transportType;

    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
