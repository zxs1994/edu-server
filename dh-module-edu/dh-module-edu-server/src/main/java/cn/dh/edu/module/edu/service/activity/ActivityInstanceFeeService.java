package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemRespVO;

import java.util.List;

/**
 * 专项活动执行实例 - 费用明细 Service
 */
public interface ActivityInstanceFeeService {

    /**
     * 费用明细分页（跨活动/实例）
     */
    PageResult<ActivityInstanceFeeItemRespVO> getFeeItemPage(ActivityInstanceFeeItemPageReqVO pageReqVO);

    /**
     * 查询实例费用明细（已结项且未生成时自动补偿生成）
     */
    List<ActivityInstanceFeeItemRespVO> getFeeItemList(Long instanceId);

    /**
     * 结项后自动生成费用明细（幂等）
     */
    void generateFeeItems(Long instanceId);

    /**
     * 学生侧积分写入完成后，将明细状态从「已入账（积分）」更新为「已付款」
     */
    void markStudentFeeItemPaid(Long feeItemId);

}
