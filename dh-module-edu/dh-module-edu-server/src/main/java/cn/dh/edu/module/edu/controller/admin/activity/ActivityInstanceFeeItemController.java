package cn.dh.edu.module.edu.controller.admin.activity;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemRespVO;
import cn.dh.edu.module.edu.service.activity.ActivityInstanceFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 专项活动费用明细")
@RestController
@RequestMapping("/edu/activity-fee-item")
@Validated
public class ActivityInstanceFeeItemController {

    @Resource
    private ActivityInstanceFeeService activityInstanceFeeService;

    @GetMapping("/page")
    @Operation(summary = "费用明细分页")
    @PreAuthorize("@ss.hasPermission('edu:activity-fee-item:query')")
    public CommonResult<PageResult<ActivityInstanceFeeItemRespVO>> getFeeItemPage(
            @Valid ActivityInstanceFeeItemPageReqVO pageReqVO) {
        return success(activityInstanceFeeService.getFeeItemPage(pageReqVO));
    }

}
