package cn.dh.edu.module.edu.process.mq;

import cn.dh.edu.common.server.process.mq.AbstractFlowMqNotificationConsumer;
import cn.dh.edu.framework.common.enums.SystemEnum;
import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import cn.dh.edu.module.edu.service.EduFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * EDU 模块统一 BPM 事件 MQ 消费者
 *
 * @author 鼎衡
 */
@Component
@ConditionalOnProperty(name = "dh.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class EduMqNotificationConsumer extends AbstractFlowMqNotificationConsumer<EduBillTypeEnum> {

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
}
