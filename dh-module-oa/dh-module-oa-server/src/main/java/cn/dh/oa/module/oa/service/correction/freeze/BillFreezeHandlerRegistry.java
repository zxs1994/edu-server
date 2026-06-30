package cn.dh.oa.module.oa.service.correction.freeze;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.CORRECTION_BILL_TYPE_NOT_SUPPORTED;

/**
 * 单据冻结处理器注册表
 */
@Component
public class BillFreezeHandlerRegistry {

    private final Map<String, BillFreezeHandler> handlerMap;

    public BillFreezeHandlerRegistry(List<BillFreezeHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(java.util.stream.Collectors.toMap(BillFreezeHandler::getBillType, h -> h, (a, b) -> a));
    }

    public BillFreezeHandler getRequired(String billType) {
        BillFreezeHandler handler = handlerMap.get(billType);
        if (handler == null) {
            throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
        return handler;
    }

    public void freeze(String billType, Long billId) {
        getRequired(billType).freeze(billId);
    }

    public void unfreeze(String billType, Long billId) {
        getRequired(billType).unfreeze(billId);
    }

    public void applyCouncilOverride(String billType, Long billId, String correctionResult) {
        getRequired(billType).applyCouncilOverride(billId, correctionResult);
    }

}
