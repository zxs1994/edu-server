package cn.dh.oa.module.oa.controller.admin.meetingroom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 会议室预约信息 Response VO")
@Data
public class MeetingRoomBookingScheduleRespVO {

    @Schema(description = "预约记录列表")
    private List<BookingItem> bookings;

    @Schema(description = "当天审批通过的预约记录列表（仅会议室信息列表打开时返回）")
    private List<BookingItem> todayApprovedBookings;

    @Schema(description = "预约时间段信息")
    @Data
    public static class BookingItem {
        @Schema(description = "预约ID", example = "1")
        private Long id;

        @Schema(description = "单据编号", example = "MRB202511130001")
        private String billCode;

        @Schema(description = "会议主题", example = "季度总结会议")
        private String meetingTitle;

        @Schema(description = "会议开始时间")
        @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
        private LocalDate meetingStartTime;

        @Schema(description = "会议结束时间")
        @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
        private LocalDate meetingEndTime;

        @Schema(description = "主持人姓名", example = "张三")
        private String moderatorName;

        @Schema(description = "申请人姓名", example = "李四")
        private String creatorName;

        @Schema(description = "单据状态", example = "2")
        private Integer processStatus;

        @Schema(description = "使用状态", example = "0")
        private Integer useStatus;
    }
}

