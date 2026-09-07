package cn.dh.edu.module.edu.controller.admin.activity;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentFeeItemRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityPaymentRequestDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.service.activity.ActivityPaymentRequestService;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 专项活动付款申请")
@RestController
@RequestMapping("/edu/activity-payment")
@Validated
public class ActivityPaymentRequestController {

    @Resource
    private ActivityPaymentRequestService paymentRequestService;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityInstanceMapper activityInstanceMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/save")
    @Operation(summary = "保存付款申请（草稿）")
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:create')")
    public CommonResult<Long> savePaymentRequest(@Valid @RequestBody ActivityPaymentRequestSaveReqVO saveReqVO) {
        return success(paymentRequestService.savePaymentRequest(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交付款申请审核")
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:submit')")
    public CommonResult<Long> submitPaymentRequest(@Valid @RequestBody ActivityPaymentRequestSaveReqVO saveReqVO) {
        return success(paymentRequestService.submitPaymentRequest(saveReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除付款申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:delete')")
    public CommonResult<Boolean> deletePaymentRequest(@RequestParam("id") Long id) {
        paymentRequestService.deletePaymentRequest(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除付款申请")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:delete')")
    public CommonResult<Boolean> deletePaymentRequestList(@RequestParam("ids") List<Long> ids) {
        paymentRequestService.deletePaymentRequestListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得付款申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:query')")
    public CommonResult<ActivityPaymentRequestRespVO> getPaymentRequest(@RequestParam("id") Long id) {
        return success(paymentRequestService.getPaymentRequest(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得付款申请分页")
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:query')")
    public CommonResult<PageResult<ActivityPaymentRequestRespVO>> getPaymentRequestPage(
            @Valid ActivityPaymentRequestPageReqVO pageReqVO) {
        PageResult<ActivityPaymentRequestDO> pageResult = paymentRequestService.getPaymentRequestPage(pageReqVO);
        PageResult<ActivityPaymentRequestRespVO> voPage =
                BeanUtils.toBean(pageResult, ActivityPaymentRequestRespVO.class);
        fillPageBrief(voPage.getList());
        return success(voPage);
    }

    /**
     * 查询可勾选的费用明细
     */
    @GetMapping("/selectable-fee-items")
    @Operation(summary = "查询可勾选的费用明细")
    @PreAuthorize("@ss.hasPermission('edu:activity-payment:query') or @ss.hasPermission('edu:activity-payment:create')")
    public CommonResult<List<ActivityPaymentFeeItemRespVO>> getSelectableFeeItems(
            @RequestParam(value = "instanceIds", required = false) List<Long> instanceIds,
            @RequestParam(value = "instanceId", required = false) Long instanceId,
            @RequestParam(value = "activityId", required = false) Long activityId,
            @RequestParam(value = "paymentRequestId", required = false) Long paymentRequestId) {
        List<Long> resolvedIds = CollUtil.isNotEmpty(instanceIds) ? instanceIds
                : (instanceId != null ? List.of(instanceId) : List.of());
        return success(paymentRequestService.getSelectableFeeItems(activityId, resolvedIds, paymentRequestId));
    }

    private void fillPageBrief(List<ActivityPaymentRequestRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> activityIds = list.stream()
                .map(ActivityPaymentRequestRespVO::getActivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityDO> activityMap = CollUtil.isEmpty(activityIds)
                ? Map.of()
                : activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityDO::getId, a -> a, (a, b) -> a));

        Set<Long> instanceIds = list.stream()
                .map(ActivityPaymentRequestRespVO::getInstanceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityInstanceDO> instanceMap = CollUtil.isEmpty(instanceIds)
                ? Map.of()
                : activityInstanceMapper.selectBatchIds(instanceIds).stream()
                .collect(Collectors.toMap(ActivityInstanceDO::getId, a -> a, (a, b) -> a));

        Set<Long> applicantIds = list.stream()
                .map(ActivityPaymentRequestRespVO::getApplicantUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(applicantIds)
                ? Map.of()
                : adminUserApi.getUserMap(applicantIds);

        for (ActivityPaymentRequestRespVO item : list) {
            // Map.of() 不允许 null key，activityId/instanceId 为空时不能直接 get
            if (item.getActivityId() != null) {
                ActivityDO activity = activityMap.get(item.getActivityId());
                if (activity != null) {
                    item.setActivityName(activity.getName());
                    item.setActivityBillCode(activity.getBillCode());
                }
            }
            if (item.getInstanceId() != null) {
                ActivityInstanceDO instance = instanceMap.get(item.getInstanceId());
                if (instance != null) {
                    item.setInstanceCode(instance.getInstanceCode());
                }
            }
            if (item.getApplicantUserId() != null) {
                AdminUserRespDTO user = userMap.get(item.getApplicantUserId());
                if (user != null && StringUtils.isNotBlank(user.getNickname())) {
                    item.setApplicantUserName(user.getNickname());
                    item.setCreatorName(user.getNickname());
                }
            }
        }
    }

}
