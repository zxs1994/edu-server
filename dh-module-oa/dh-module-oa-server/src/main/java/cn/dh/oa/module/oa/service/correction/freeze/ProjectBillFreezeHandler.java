package cn.dh.oa.module.oa.service.correction.freeze;

import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.module.oa.dal.mysql.project.ProjectInitiationBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.REJECT;

@Slf4j
@Component
public class ProjectBillFreezeHandler implements BillFreezeHandler {

    @Resource
    private ProjectInitiationBillMapper projectInitiationBillMapper;

    @Override
    public String getBillType() {
        return OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getProcessDefinitionKey();
    }

    @Override
    public void freeze(Long billId) {
        log.info("[ProjectBillFreezeHandler] 冻结立项 billId={}", billId);
    }

    @Override
    public void unfreeze(Long billId) {
        log.info("[ProjectBillFreezeHandler] 解冻立项 billId={}", billId);
    }

    @Override
    public void applyCouncilOverride(Long billId, String correctionResult) {
        projectInitiationBillMapper.updateById(new ProjectInitiationBillDO()
                .setId(billId).setProcessStatus(REJECT.getStatus()));
        log.info("[ProjectBillFreezeHandler] 理事会决议推翻立项 billId={}", billId);
    }

}
