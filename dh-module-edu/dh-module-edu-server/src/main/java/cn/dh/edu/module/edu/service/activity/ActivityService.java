package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivitySaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 专项活动 Service
 *
 * @author 鼎衡
 */
public interface ActivityService {

    Long saveActivity(@Valid ActivitySaveReqVO saveReqVO);

    Long submitActivity(@Valid ActivitySaveReqVO saveReqVO);

    void deleteActivity(Long id);

    void deleteActivityListByIds(List<Long> ids);

    ActivityRespVO getActivity(Long id);

    PageResult<ActivityDO> getActivityPage(ActivityPageReqVO pageReqVO);

    /**
     * 参与人排序：教培在前，学生在后
     */
    List<Long> sortParticipantUserIds(List<Long> userIds);

}
