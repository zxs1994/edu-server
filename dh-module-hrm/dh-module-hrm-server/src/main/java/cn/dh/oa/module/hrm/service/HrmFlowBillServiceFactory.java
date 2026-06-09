package cn.dh.oa.module.hrm.service;

import cn.dh.oa.framework.common.service.FlowBillServiceFactory;
import cn.dh.oa.module.hrm.enums.HrmBillTypeEnum;
import org.springframework.stereotype.Component;

/**
 * HRM 流程表单服务工厂
 *
 * @author 鼎衡
 */
@Component
public class HrmFlowBillServiceFactory extends FlowBillServiceFactory<HrmBillTypeEnum> {

    @Override
    protected HrmBillTypeEnum[] getBillTypeValues() {
        return HrmBillTypeEnum.values();
    }
}

