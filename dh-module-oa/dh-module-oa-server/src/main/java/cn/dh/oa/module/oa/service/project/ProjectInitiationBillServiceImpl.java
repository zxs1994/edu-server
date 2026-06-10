package cn.dh.oa.module.oa.service.project;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.oa.framework.common.service.FlowBillService;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import java.util.*;

import cn.dh.oa.module.oa.controller.admin.project.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.oa.dal.mysql.project.ProjectInitiationBillMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

@Slf4j
@Service
@Validated
public class ProjectInitiationBillServiceImpl implements ProjectInitiationBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private ProjectInitiationBillMapper projectInitiationBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveProjectInitiationBill(ProjectInitiationBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_PROJECT_INITIATION_BILL));
        }
        ProjectInitiationBillDO bill = BeanUtils.toBean(saveReqVO, ProjectInitiationBillDO.class);
        projectInitiationBillMapper.insertOrUpdate(bill);
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        return bill.getId();
    }

    @Override
    public Long submitProjectInitiationBill(ProjectInitiationBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_PROJECT_INITIATION_BILL));
        }
        ProjectInitiationBillDO bill = BeanUtils.toBean(saveReqVO, ProjectInitiationBillDO.class).setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        projectInitiationBillMapper.insertOrUpdate(bill);
        Map<String, Object> vars = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 项目立项单特有流程变量：是否重大
        vars.put(PV_PROJECT_IS_MAJOR, saveReqVO.getIsMajor());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getProcessDefinitionKey())
                        .setVariables(vars)
                        .setBusinessKey(String.valueOf(bill.getId()))
        ).getCheckedData();
        projectInitiationBillMapper.updateById(new ProjectInitiationBillDO().setId(bill.getId()).setProcessInstanceId(processInstanceId));
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        return bill.getId();
    }

    @Override
    public Long createProjectInitiationBill(ProjectInitiationBillSaveReqVO createReqVO) {
        ProjectInitiationBillDO bill = BeanUtils.toBean(createReqVO, ProjectInitiationBillDO.class);
        projectInitiationBillMapper.insert(bill);
        return bill.getId();
    }

    @Override
    public void updateProjectInitiationBill(ProjectInitiationBillSaveReqVO updateReqVO) {
        validateProjectInitiationBillExists(updateReqVO.getId());
        ProjectInitiationBillDO updateObj = BeanUtils.toBean(updateReqVO, ProjectInitiationBillDO.class);
        projectInitiationBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteProjectInitiationBill(Long id) {
        validateProjectInitiationBillExists(id);
        projectInitiationBillMapper.deleteById(id);
    }

    @Override
    public void deleteProjectInitiationBillListByIds(List<Long> ids) {
        ids.forEach(this::validateProjectInitiationBillExists);
        projectInitiationBillMapper.deleteByIds(ids);
    }

    @Override
    public ProjectInitiationBillDO getProjectInitiationBill(Long id) {
        return projectInitiationBillMapper.selectById(id);
    }

    @Override
    public ProjectInitiationBillRespVO getProjectInitiationBillInfo(Long id) {
        ProjectInitiationBillDO bill = projectInitiationBillMapper.selectById(id);
        if (bill == null) {
            return null;
        }
        ProjectInitiationBillRespVO respVO = BeanUtils.toBean(bill, ProjectInitiationBillRespVO.class);
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        return respVO;
    }

    @Override
    public PageResult<ProjectInitiationBillDO> getProjectInitiationBillPage(ProjectInitiationBillPageReqVO pageReqVO) {
        return projectInitiationBillMapper.selectPage(pageReqVO);
    }

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_PROJECT_INITIATION_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        projectInitiationBillMapper.updateById(new ProjectInitiationBillDO().setId(id).setProcessStatus(status));
    }

    private void validateProjectInitiationBillExists(Long id) {
        if (projectInitiationBillMapper.selectById(id) == null) {
            throw exception(PROJECT_INITIATION_BILL_NOT_EXISTS);
        }
    }

}
