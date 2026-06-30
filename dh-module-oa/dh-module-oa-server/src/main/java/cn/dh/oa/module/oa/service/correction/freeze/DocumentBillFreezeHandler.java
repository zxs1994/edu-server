package cn.dh.oa.module.oa.service.correction.freeze;

import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.module.oa.dal.mysql.document.DocumentDispatchBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.REJECT;

@Slf4j
@Component
public class DocumentBillFreezeHandler implements BillFreezeHandler {

    @Resource
    private DocumentDispatchBillMapper documentDispatchBillMapper;

    @Override
    public String getBillType() {
        return OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getProcessDefinitionKey();
    }

    @Override
    public void freeze(Long billId) {
        log.info("[DocumentBillFreezeHandler] 冻结发文 billId={}", billId);
    }

    @Override
    public void unfreeze(Long billId) {
        log.info("[DocumentBillFreezeHandler] 解冻发文 billId={}", billId);
    }

    @Override
    public void applyCouncilOverride(Long billId, String correctionResult) {
        documentDispatchBillMapper.updateById(new DocumentDispatchBillDO()
                .setId(billId).setProcessStatus(REJECT.getStatus()));
        log.info("[DocumentBillFreezeHandler] 理事会决议推翻发文 billId={}", billId);
    }

}
