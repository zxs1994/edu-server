package cn.dh.edu.module.edu.process.local;

import cn.dh.edu.common.server.process.listener.AbstractFlowLocalNotificationListener;
import cn.dh.edu.framework.common.enums.SystemEnum;
import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import cn.dh.edu.module.edu.service.EduFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * EDU 模块 BPM 本地事件监听
 *
 * @author 鼎衡
 */
@Component
public class EduLocalNotificationListener extends AbstractFlowLocalNotificationListener<EduBillTypeEnum> {

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
