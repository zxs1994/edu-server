package cn.dh.edu.module.edu.controller.admin.activity.vo;

import cn.dh.edu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 我的活动（学生）分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActivityInstanceMyPageReqVO extends PageParam {

    @Schema(description = "实例编号（模糊）")
    private String instanceCode;

    @Schema(description = "活动名称（模糊）")
    private String activityName;

    @Schema(description = "是否已报名")
    private Boolean enrolled;

    @Schema(description = "是否可报名")
    private Boolean canEnroll;

    /**
     * 我的反馈：pending-待填写 submitted-已提交 waiting-未开始
     */
    @Schema(description = "我的反馈筛选")
    private String myFeedback;

}
