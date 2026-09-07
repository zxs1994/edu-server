package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 专项活动执行实例详情 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityInstanceDetailRespVO extends ActivityInstanceRespVO {

    @Schema(description = "关联主活动信息")
    private ActivityRespVO activity;

    @Schema(description = "活动记录")
    private ActivityInstanceRecordRespVO record;

    @Schema(description = "活动记录是否可编辑")
    private Boolean recordEditable;

    @Schema(description = "学生报名状态列表（提醒报名 Tab）")
    private List<ActivityInstanceEnrollmentParticipantRespVO> enrollmentParticipants;

    @Schema(description = "是否处于报名中")
    private Boolean enrolling;

    @Schema(description = "活动反馈")
    private ActivityInstanceFeedbackRespVO feedback;

}
