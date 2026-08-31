package cn.dh.edu.module.edu.process.feign;

import cn.dh.edu.common.server.process.controller.AbstractFlowNotificationController;
import cn.dh.edu.framework.common.enums.SystemEnum;
import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.dh.edu.module.edu.enums.ApiConstants;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import cn.dh.edu.module.edu.service.EduFlowBillServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EDU 模块 BPM 回调（Feign）
 *
 * @author 鼎衡
 */
@Tag(name = "管理后台 - EDU流程回调")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/process-callback")
@Validated
@Slf4j
public class EduFeignNotificationController extends AbstractFlowNotificationController<EduBillTypeEnum> {

    @Resource
    private EduFlowBillServiceFactory flowBillServiceFactory;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.EDU;
    }

    @Override
    protected FlowBillServiceFactory<EduBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }

    @PostMapping("/bpm-event")
    @Operation(summary = "接收BPM事件回调（支持流程实例和任务事件）")
    public CommonResult<Boolean> handleBpmEvent(@RequestBody BpmProcessInstanceStatusMessage message) {
        return doHandleBpmEvent(message);
    }

    @PostMapping("/status-change")
    @Deprecated
    @Operation(summary = "接收流程状态变化回调（兼容旧版本）")
    public CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message) {
        return doHandleProcessStatusChange(message);
    }
}
