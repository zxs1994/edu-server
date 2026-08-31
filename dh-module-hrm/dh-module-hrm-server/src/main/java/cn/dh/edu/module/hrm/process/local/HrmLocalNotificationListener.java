package cn.dh.edu.module.hrm.process.local;

import cn.dh.edu.common.server.process.listener.AbstractFlowLocalNotificationListener;
import cn.dh.edu.framework.common.enums.SystemEnum;
import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.hrm.enums.HrmBillTypeEnum;
import cn.dh.edu.module.hrm.service.HrmFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * HRM 模块 BPM 本地事件监听
 *
 * @author 鼎衡
 */
@Component
public class HrmLocalNotificationListener extends AbstractFlowLocalNotificationListener<HrmBillTypeEnum> {

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

