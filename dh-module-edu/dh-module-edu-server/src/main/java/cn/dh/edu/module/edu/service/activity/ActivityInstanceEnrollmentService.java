package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollableRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceH5EnrollInfoRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceMyPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollmentParticipantRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollmentRespVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import cn.dh.edu.module.edu.util.ActivityInstanceStatusUtils;

import java.util.List;

/**
 * 专项活动执行实例 - 报名 Service
 */
public interface ActivityInstanceEnrollmentService {

    /**
     * 回填实例运行时状态（不落库）
     */
    void fillRuntimeStatus(ActivityInstanceDO instance, ActivityDO activity);

    /**
     * 当前登录用户可报名/已报名实例分页
     */
    PageResult<ActivityInstanceEnrollableRespVO> getMyEnrollablePage(ActivityInstanceMyPageReqVO pageReqVO);

    /**
     * 实例报名列表（仅已报名记录）
     */
    List<ActivityInstanceEnrollmentRespVO> getEnrollmentList(Long instanceId);

    /**
     * 实例参与人报名状态列表（管理端提醒报名 Tab，仅学生）
     */
    List<ActivityInstanceEnrollmentParticipantRespVO> getEnrollmentParticipantList(Long instanceId);

    /**
     * 当前用户报名
     */
    void enroll(ActivityInstanceEnrollReqVO reqVO);

    /**
     * 当前用户取消报名
     */
    void cancelEnroll(ActivityInstanceEnrollReqVO reqVO);

    /**
     * H5 免登：按报名 token 查询报名信息
     */
    ActivityInstanceH5EnrollInfoRespVO getH5EnrollInfo(String token);

    /**
     * H5 免登：按报名 token 提交报名
     */
    void enrollByToken(String token);

}
