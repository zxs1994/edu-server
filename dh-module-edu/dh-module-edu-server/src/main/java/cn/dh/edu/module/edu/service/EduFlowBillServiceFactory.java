package cn.dh.edu.module.edu.service;

import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import org.springframework.stereotype.Component;

/**
 * EDU 流程表单服务工厂
 *
 * @author 鼎衡
 */
@Component
public class EduFlowBillServiceFactory extends FlowBillServiceFactory<EduBillTypeEnum> {

    @Override
    protected EduBillTypeEnum[] getBillTypeValues() {
        return EduBillTypeEnum.values();
    }
}
