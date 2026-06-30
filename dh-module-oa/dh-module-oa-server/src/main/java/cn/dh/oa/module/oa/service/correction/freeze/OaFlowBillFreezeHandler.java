package cn.dh.oa.module.oa.service.correction.freeze;

import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.correction.BillCorrectionSourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 协同办公流程单据通用冻结处理器（无专属 Handler 的单据走此逻辑）
 */
@Slf4j
@Component
public class OaFlowBillFreezeHandler {

    @Resource
    private BillCorrectionSourceService billCorrectionSourceService;

    public boolean supports(String billType) {
        return OaBillTypeEnum.isPresidentCorrectionSupported(billType);
    }

    public void freeze(String billType, Long billId) {
        log.info("[OaFlowBillFreezeHandler] 冻结 billType={}, billId={}", billType, billId);
    }

    public void unfreeze(String billType, Long billId) {
        log.info("[OaFlowBillFreezeHandler] 解冻 billType={}, billId={}", billType, billId);
    }

    public void applyCouncilOverride(String billType, Long billId, String correctionResult) {
        billCorrectionSourceService.applyCouncilReject(billType, billId);
        log.info("[OaFlowBillFreezeHandler] 理事会决议推翻 billType={}, billId={}, result={}",
                billType, billId, correctionResult);
    }

}
