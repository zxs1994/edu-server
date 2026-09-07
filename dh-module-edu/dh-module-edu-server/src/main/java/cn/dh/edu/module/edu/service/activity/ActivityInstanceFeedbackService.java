package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.module.edu.controller.admin.activity.vo.*;

public interface ActivityInstanceFeedbackService {

    ActivityInstanceFeedbackRespVO getFeedbackDetail(Long instanceId);

    void saveStudentFeedback(ActivityInstanceStudentFeedbackSaveReqVO saveReqVO);

    void saveTeacherFeedback(ActivityInstanceTeacherFeedbackSaveReqVO saveReqVO);

    void saveAdminFeedback(ActivityInstanceAdminFeedbackSaveReqVO saveReqVO);

    /**
     * 当前用户是否可提交学生反馈
     */
    boolean canSubmitStudentFeedback(Long instanceId, Long userId);

    /**
     * 当前用户的学生反馈（未提交则返回 null）
     */
    ActivityInstanceStudentFeedbackRespVO getMyStudentFeedback(Long instanceId, Long userId);

}
