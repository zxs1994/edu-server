package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentFeeItemRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityPaymentRequestDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 专项活动付款申请 Service
 */
public interface ActivityPaymentRequestService {

    Long savePaymentRequest(@Valid ActivityPaymentRequestSaveReqVO saveReqVO);

    Long submitPaymentRequest(@Valid ActivityPaymentRequestSaveReqVO saveReqVO);

    void deletePaymentRequest(Long id);

    void deletePaymentRequestListByIds(List<Long> ids);

    ActivityPaymentRequestRespVO getPaymentRequest(Long id);

    PageResult<ActivityPaymentRequestDO> getPaymentRequestPage(ActivityPaymentRequestPageReqVO pageReqVO);

    /**
     * 查询可勾选的费用明细（必须指定活动实例；教培/学生侧统一走付款申请）
     *
     * @param activityId       活动ID（可空；传入时仅保留该活动下实例的明细，跨活动多选时不传）
     * @param instanceIds      活动实例ID列表（可跨专项活动）
     * @param paymentRequestId 可选，当前草稿单（含已绑定明细）
     */
    List<ActivityPaymentFeeItemRespVO> getSelectableFeeItems(Long activityId, List<Long> instanceIds,
                                                             Long paymentRequestId);

}
