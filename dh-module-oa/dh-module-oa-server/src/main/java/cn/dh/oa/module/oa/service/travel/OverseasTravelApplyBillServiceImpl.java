package cn.dh.oa.module.oa.service.travel;

import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 出境差旅申请单 Service 实现类
 * 继承 TravelApplyBillServiceImpl，复用 CRUD 逻辑
 * 仅用于 FlowBillService 工厂注册，让 BPM 回调能正确路由到出境差旅申请单
 */
@Slf4j
@Service
@Validated
public class OverseasTravelApplyBillServiceImpl extends TravelApplyBillServiceImpl {

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_OVERSEAS_TRAVEL_APPLY_BILL;
    }

}
