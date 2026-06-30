package cn.dh.oa.module.oa.process.local;

import cn.dh.oa.module.bpm.api.event.BpmEventTypeEnum;
import cn.dh.oa.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 会长纠错：重审流程完成后的收尾
 */
@Slf4j
@Component
public class OaPresidentCorrectionCompletionListener implements ApplicationListener<BpmProcessInstanceStatusEvent> {

    @Resource
    private OaPresidentCorrectionApi oaPresidentCorrectionApi;

    @Override
    public void onApplicationEvent(BpmProcessInstanceStatusEvent event) {
        if (event.getProcessInstanceInfo() == null || event.getEventType() == null) {
            return;
        }
        if (event.getEventType() != BpmEventTypeEnum.PROCESS_INSTANCE_COMPLETED) {
            return;
        }
        Integer status = event.getProcessInstanceInfo().getStatus();
        if (!BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(status)) {
            return;
        }
        oaPresidentCorrectionApi.onReApprovalProcessCompleted(
                event.getProcessInstanceInfo().getProcessDefinitionKey(),
                event.getProcessInstanceInfo().getBusinessKey(),
                event.getProcessInstanceInfo().getProcessInstanceId(),
                status);
    }

}
