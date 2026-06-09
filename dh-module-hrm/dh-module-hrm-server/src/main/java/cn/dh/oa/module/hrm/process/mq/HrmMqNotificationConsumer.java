package cn.dh.oa.module.hrm.process.mq;

import cn.dh.oa.common.server.process.mq.AbstractFlowMqNotificationConsumer;
import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.service.FlowBillServiceFactory;
import cn.dh.oa.module.hrm.enums.HrmBillTypeEnum;
import cn.dh.oa.module.hrm.service.HrmFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * HRM 模块统一BPM事件MQ消费者
 * 支持流程实例事件和任务事件的统一处理
 *
 * @author 鼎衡
 */
@Component
@ConditionalOnProperty(name = "dh.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class HrmMqNotificationConsumer extends AbstractFlowMqNotificationConsumer<HrmBillTypeEnum> {

    @Resource
    private HrmFlowBillServiceFactory flowBillServiceFactory;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.HRM;
    }

    @Override
    protected FlowBillServiceFactory<HrmBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }
}

