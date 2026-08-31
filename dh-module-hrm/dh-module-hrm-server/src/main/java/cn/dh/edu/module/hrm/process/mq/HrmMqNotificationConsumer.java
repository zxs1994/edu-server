package cn.dh.edu.module.hrm.process.mq;

import cn.dh.edu.common.server.process.mq.AbstractFlowMqNotificationConsumer;
import cn.dh.edu.framework.common.enums.SystemEnum;
import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.hrm.enums.HrmBillTypeEnum;
import cn.dh.edu.module.hrm.service.HrmFlowBillServiceFactory;
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

