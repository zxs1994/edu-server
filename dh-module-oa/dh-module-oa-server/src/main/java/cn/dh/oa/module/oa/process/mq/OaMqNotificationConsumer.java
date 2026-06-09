package cn.dh.oa.module.oa.process.mq;

import cn.dh.oa.common.server.process.mq.AbstractFlowMqNotificationConsumer;
import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.service.FlowBillServiceFactory;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.OaFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OA 模块统一BPM事件MQ消费者
 * 支持流程实例事件和任务事件的统一处理
 *
 * @author 鼎衡
 */
@Component
@ConditionalOnProperty(name = "dh.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class OaMqNotificationConsumer extends AbstractFlowMqNotificationConsumer<OaBillTypeEnum> {

    @Resource
    private OaFlowBillServiceFactory flowBillServiceFactory;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.OA;
    }

    @Override
    protected FlowBillServiceFactory<OaBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }
}
