package cn.dh.edu.module.edu.controller.admin.activity;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceAdminFeedbackSaveReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceDetailRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollableRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceEnrollmentRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceMyPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstancePageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceRecordSaveReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceStudentFeedbackSaveReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceStudentFeedbackRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceTeacherFeedbackSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.service.activity.ActivityInstanceEnrollmentService;
import cn.dh.edu.module.edu.service.activity.ActivityInstanceFeedbackService;
import cn.dh.edu.module.edu.service.activity.ActivityInstanceFeeService;
import cn.dh.edu.module.edu.service.activity.ActivityInstanceService;
import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 专项活动实例")
@RestController
@RequestMapping("/edu/activity-instance")
@Validated
public class ActivityInstanceController {

    @Resource
    private ActivityInstanceService activityInstanceService;
    @Resource
    private ActivityInstanceEnrollmentService activityInstanceEnrollmentService;
    @Resource
    private ActivityInstanceFeedbackService activityInstanceFeedbackService;
    @Resource
    private ActivityInstanceFeeService activityInstanceFeeService;
    @Resource
    private ActivityMapper activityMapper;

    @GetMapping("/get")
    @Operation(summary = "获得专项活动实例详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:query')")
    public CommonResult<ActivityInstanceDetailRespVO> getActivityInstance(@RequestParam("id") Long id) {
        return success(activityInstanceService.getActivityInstanceDetail(id));
    }

    @PostMapping("/save-record")
    @Operation(summary = "保存活动记录")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:update')")
    public CommonResult<Boolean> saveActivityInstanceRecord(
            @Valid @RequestBody ActivityInstanceRecordSaveReqVO saveReqVO) {
        activityInstanceService.saveActivityInstanceRecord(saveReqVO);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得专项活动实例分页")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:query')")
    public CommonResult<PageResult<ActivityInstanceRespVO>> getActivityInstancePage(
            @Valid ActivityInstancePageReqVO pageReqVO) {
        if (pageReqVO.getActivityId() != null) {
            activityInstanceService.ensureInstancesForApprovedActivity(pageReqVO.getActivityId());
        }
        PageResult<ActivityInstanceDO> pageResult = activityInstanceService.getActivityInstancePage(pageReqVO);
        PageResult<ActivityInstanceRespVO> voPage = BeanUtils.toBean(pageResult, ActivityInstanceRespVO.class);
        fillActivityInfo(voPage.getList());
        return success(voPage);
    }

    @PostMapping("/generate")
    @Operation(summary = "为已审批活动生成活动实例（补偿）")
    @Parameter(name = "activityId", description = "活动ID", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:query')")
    public CommonResult<Boolean> generateForActivity(@RequestParam("activityId") Long activityId) {
        activityInstanceService.ensureInstancesForApprovedActivity(activityId);
        return success(true);
    }

    @GetMapping("/my-enrollable-page")
    @Operation(summary = "当前用户我的活动分页")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:enroll')")
    public CommonResult<PageResult<ActivityInstanceEnrollableRespVO>> getMyEnrollablePage(
            @Valid ActivityInstanceMyPageReqVO pageReqVO) {
        return success(activityInstanceEnrollmentService.getMyEnrollablePage(pageReqVO));
    }

    @GetMapping("/enrollment-list")
    @Operation(summary = "实例报名列表")
    @Parameter(name = "instanceId", description = "实例ID", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:query')")
    public CommonResult<List<ActivityInstanceEnrollmentRespVO>> getEnrollmentList(
            @RequestParam("instanceId") Long instanceId) {
        return success(activityInstanceEnrollmentService.getEnrollmentList(instanceId));
    }

    @PostMapping("/enroll")
    @Operation(summary = "当前用户报名")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:enroll')")
    public CommonResult<Boolean> enroll(@Valid @RequestBody ActivityInstanceEnrollReqVO reqVO) {
        activityInstanceEnrollmentService.enroll(reqVO);
        return success(true);
    }

    @PostMapping("/cancel-enroll")
    @Operation(summary = "当前用户取消报名")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:enroll')")
    public CommonResult<Boolean> cancelEnroll(@Valid @RequestBody ActivityInstanceEnrollReqVO reqVO) {
        activityInstanceEnrollmentService.cancelEnroll(reqVO);
        return success(true);
    }

    @PostMapping("/save-student-feedback")
    @Operation(summary = "提交学生反馈")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:enroll')")
    public CommonResult<Boolean> saveStudentFeedback(
            @Valid @RequestBody ActivityInstanceStudentFeedbackSaveReqVO saveReqVO) {
        activityInstanceFeedbackService.saveStudentFeedback(saveReqVO);
        return success(true);
    }

    @GetMapping("/my-student-feedback")
    @Operation(summary = "当前用户的学生反馈")
    @Parameter(name = "instanceId", description = "实例ID", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:enroll')")
    public CommonResult<ActivityInstanceStudentFeedbackRespVO> getMyStudentFeedback(
            @RequestParam("instanceId") Long instanceId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(activityInstanceFeedbackService.getMyStudentFeedback(instanceId, userId));
    }

    @PostMapping("/save-teacher-feedback")
    @Operation(summary = "提交教培反馈")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:enroll') or @ss.hasPermission('edu:activity-instance:update')")
    public CommonResult<Boolean> saveTeacherFeedback(
            @Valid @RequestBody ActivityInstanceTeacherFeedbackSaveReqVO saveReqVO) {
        activityInstanceFeedbackService.saveTeacherFeedback(saveReqVO);
        return success(true);
    }

    @PostMapping("/save-admin-feedback")
    @Operation(summary = "提交班务评价（仅活动负责人）")
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:query')")
    public CommonResult<Boolean> saveAdminFeedback(
            @Valid @RequestBody ActivityInstanceAdminFeedbackSaveReqVO saveReqVO) {
        activityInstanceFeedbackService.saveAdminFeedback(saveReqVO);
        return success(true);
    }

    @GetMapping("/fee-list")
    @Operation(summary = "实例费用明细列表")
    @Parameter(name = "instanceId", description = "实例ID", required = true)
    @PreAuthorize("@ss.hasPermission('edu:activity-instance:query')")
    public CommonResult<List<ActivityInstanceFeeItemRespVO>> getFeeItemList(
            @RequestParam("instanceId") Long instanceId) {
        return success(activityInstanceFeeService.getFeeItemList(instanceId));
    }

    private void fillActivityInfo(List<ActivityInstanceRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> activityIds = list.stream()
                .map(ActivityInstanceRespVO::getActivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(activityIds)) {
            return;
        }
        Map<Long, ActivityDO> activityMap = activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityDO::getId, item -> item, (a, b) -> a));
        for (ActivityInstanceRespVO item : list) {
            ActivityDO activity = activityMap.get(item.getActivityId());
            if (activity == null) {
                continue;
            }
            item.setActivityBillCode(activity.getBillCode());
            item.setActivityName(activity.getName());
        }
    }

}
