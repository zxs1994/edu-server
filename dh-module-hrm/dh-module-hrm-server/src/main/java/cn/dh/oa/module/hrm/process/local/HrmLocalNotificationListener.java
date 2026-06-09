package cn.dh.oa.module.hrm.process.local;

import cn.dh.oa.common.server.process.listener.AbstractFlowLocalNotificationListener;
import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.service.FlowBillServiceFactory;
import cn.dh.oa.module.hrm.enums.HrmBillTypeEnum;
import cn.dh.oa.module.hrm.service.HrmFlowBillServiceFactory;
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

