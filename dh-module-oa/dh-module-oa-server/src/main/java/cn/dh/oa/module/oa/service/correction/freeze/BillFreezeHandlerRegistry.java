package cn.dh.oa.module.oa.service.correction.freeze;

import jakarta.annotation.Resource;
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
    @Resource
    private OaFlowBillFreezeHandler oaFlowBillFreezeHandler;

    public BillFreezeHandlerRegistry(List<BillFreezeHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(java.util.stream.Collectors.toMap(BillFreezeHandler::getBillType, h -> h, (a, b) -> a));
    }

    public BillFreezeHandler getRequired(String billType) {
        BillFreezeHandler handler = handlerMap.get(billType);
        if (handler != null) {
            return handler;
        }
        if (oaFlowBillFreezeHandler.supports(billType)) {
            return null;
        }
        throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
    }

    public void freeze(String billType, Long billId) {
        BillFreezeHandler handler = handlerMap.get(billType);
        if (handler != null) {
            handler.freeze(billId);
            return;
        }
        if (oaFlowBillFreezeHandler.supports(billType)) {
            oaFlowBillFreezeHandler.freeze(billType, billId);
            return;
        }
        throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
    }

    public void unfreeze(String billType, Long billId) {
        BillFreezeHandler handler = handlerMap.get(billType);
        if (handler != null) {
            handler.unfreeze(billId);
            return;
        }
        if (oaFlowBillFreezeHandler.supports(billType)) {
            oaFlowBillFreezeHandler.unfreeze(billType, billId);
            return;
        }
        throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
    }

    public void applyCouncilOverride(String billType, Long billId, String correctionResult) {
        BillFreezeHandler handler = handlerMap.get(billType);
        if (handler != null) {
            handler.applyCouncilOverride(billId, correctionResult);
            return;
        }
        if (oaFlowBillFreezeHandler.supports(billType)) {
            oaFlowBillFreezeHandler.applyCouncilOverride(billType, billId, correctionResult);
            return;
        }
        throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
    }

}
