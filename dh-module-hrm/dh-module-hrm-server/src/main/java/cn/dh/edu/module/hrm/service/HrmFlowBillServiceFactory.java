package cn.dh.edu.module.hrm.service;

import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.hrm.enums.HrmBillTypeEnum;
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

