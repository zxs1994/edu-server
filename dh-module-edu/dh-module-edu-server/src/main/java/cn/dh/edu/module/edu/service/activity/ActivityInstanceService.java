package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceDetailRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstancePageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceRecordSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;

/**
 * 专项活动执行实例 Service
 */
public interface ActivityInstanceService {

    void generateInstancesForActivity(Long activityId);

    void ensureInstancesForApprovedActivity(Long activityId);

    ActivityInstanceDetailRespVO getActivityInstanceDetail(Long id);

    PageResult<ActivityInstanceDO> getActivityInstancePage(ActivityInstancePageReqVO pageReqVO);

    void saveActivityInstanceRecord(ActivityInstanceRecordSaveReqVO saveReqVO);

    /**
     * 回填实例运行时状态（不落库）
     */
    void fillRuntimeStatus(ActivityInstanceDO instance, ActivityDO activity);

}
