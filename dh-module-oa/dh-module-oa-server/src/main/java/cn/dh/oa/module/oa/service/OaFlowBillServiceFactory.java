package cn.dh.oa.module.oa.service;

import cn.dh.oa.framework.common.service.FlowBillServiceFactory;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import org.springframework.stereotype.Component;

/**
 * OA流程表单服务工厂类
 * 
 * @author 鼎衡
 */
@Component
public class OaFlowBillServiceFactory extends FlowBillServiceFactory<OaBillTypeEnum> {

    @Override
    protected OaBillTypeEnum[] getBillTypeValues() {
        return OaBillTypeEnum.values();
    }
}
