package cn.dh.oa.module.oa.process.fegin;

import cn.dh.oa.common.server.process.controller.AbstractFlowNotificationController;
import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.service.FlowBillServiceFactory;
import cn.dh.oa.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import cn.dh.oa.module.oa.enums.ApiConstants;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.OaFlowBillServiceFactory;
import cn.dh.oa.module.oa.service.correction.BillCorrectionSourceService;
import cn.hutool.core.util.StrUtil;
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
 * OA 流程回调 Controller
 * 接收来自BPM服务的Feign调用
 *
 * @author 鼎衡
 */
@Tag(name = "管理后台 - OA流程回调")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/process-callback")
@Validated
@Slf4j
public class OaFeignNotificationController extends AbstractFlowNotificationController<OaBillTypeEnum> {

    @Resource
    private OaFlowBillServiceFactory flowBillServiceFactory;
    @Resource
    private OaPresidentCorrectionApi oaPresidentCorrectionApi;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.OA;
    }

    @Override
    protected FlowBillServiceFactory<OaBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }

    @Override
    protected CommonResult<Boolean> handleProcessInstanceEvent(BpmProcessInstanceStatusMessage message) {
        String processDefinitionKey = message.getProcessDefinitionKey();
        String businessKey = message.getBusinessKey();
        if (StrUtil.isNotBlank(businessKey)
                && BillCorrectionSourceService.SUPPORTED_BILL_TYPES.contains(processDefinitionKey)) {
            Long billId = Long.parseLong(businessKey);
            if (oaPresidentCorrectionApi.isBillFrozen(processDefinitionKey, billId)) {
                String processInstanceId = message.getProcessInstanceId();
                if (!oaPresidentCorrectionApi.shouldSyncFrozenBillProcessStatus(
                        processDefinitionKey, billId, processInstanceId)) {
                    log.info("[handleProcessInstanceEvent] 会长纠错冻结中，跳过非重审流程状态同步 billId={}, pi={}",
                            billId, processInstanceId);
                    return CommonResult.success(true);
                }
            }
        }
        return super.handleProcessInstanceEvent(message);
    }

    @PostMapping("/bpm-event")
    @Operation(summary = "接收BPM事件回调（支持流程实例和任务事件）")
    public CommonResult<Boolean> handleBpmEvent(@RequestBody BpmProcessInstanceStatusMessage message) {
        return doHandleBpmEvent(message);
    }

    @PostMapping("/status-change")
    @Operation(summary = "接收流程状态变化回调（兼容旧版本）")
    @Deprecated
    public CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message) {
        return doHandleProcessStatusChange(message);
    }
} 