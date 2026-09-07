package cn.dh.edu.module.edu.controller.admin.activity;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.tenant.core.aop.TenantIgnore;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceH5EnrollInfoRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceH5EnrollReqVO;
import cn.dh.edu.module.edu.service.activity.ActivityInstanceEnrollmentService;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.framework.common.pojo.CommonResult.success;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID;

@Tag(name = "管理后台 - 专项活动 H5 报名（公开）")
@RestController
@RequestMapping("/edu/activity-instance/h5")
@Validated
@PermitAll
public class ActivityInstanceH5EnrollController {

    @Resource
    private ActivityInstanceEnrollmentService activityInstanceEnrollmentService;

    @GetMapping("/enroll-info")
    @TenantIgnore
    @Operation(summary = "H5 免登查询报名信息")
    @Parameter(name = "c", description = "短码", required = true)
    public CommonResult<ActivityInstanceH5EnrollInfoRespVO> getEnrollInfo(
            @RequestParam("c") String c) {
        if (StrUtil.isBlank(c)) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_TOKEN_INVALID);
        }
        return success(activityInstanceEnrollmentService.getH5EnrollInfo(c.trim()));
    }

    @PostMapping("/enroll")
    @TenantIgnore
    @Operation(summary = "H5 免登提交报名")
    public CommonResult<Boolean> enroll(@Valid @RequestBody ActivityInstanceH5EnrollReqVO reqVO) {
        activityInstanceEnrollmentService.enrollByToken(reqVO.getC().trim());
        return success(true);
    }

}
