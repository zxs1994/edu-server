package cn.dh.oa.module.hrm.service.bill;

import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.hrm.api.bill.HrmDraftBillApi;
import cn.dh.oa.module.hrm.api.bill.dto.HrmDraftBillQueryDTO;
import cn.dh.oa.module.hrm.api.bill.dto.HrmDraftBillRespDTO;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeEntryBillDO;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeRegularBillDO;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeResignationBillDO;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeTransferBillDO;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeEntryBillMapper;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeRegularBillMapper;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeResignationBillMapper;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeTransferBillMapper;
import cn.dh.oa.module.hrm.enums.HrmBillTypeEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 查询 HRM 未提交草稿单据
 */
@Service
public class HrmDraftBillService implements HrmDraftBillApi {

    @Resource
    private EmployeeEntryBillMapper employeeEntryBillMapper;
    @Resource
    private EmployeeRegularBillMapper employeeRegularBillMapper;
    @Resource
    private EmployeeTransferBillMapper employeeTransferBillMapper;
    @Resource
    private EmployeeResignationBillMapper employeeResignationBillMapper;

    @Override
    public List<HrmDraftBillRespDTO> listMyDraftBills(Long userId, HrmDraftBillQueryDTO query) {
        if (userId == null) {
            return List.of();
        }
        HrmDraftBillQueryDTO safeQuery = query != null ? query : new HrmDraftBillQueryDTO();
        List<HrmDraftBillRespDTO> result = new ArrayList<>();
        String creator = String.valueOf(userId);
        Integer notStart = BpmProcessInstanceStatusEnum.NOT_START.getStatus();

        if (shouldQuery(HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL.getProcessDefinitionKey(), safeQuery)) {
            employeeEntryBillMapper.selectList(draftWrapper(EmployeeEntryBillDO.class, creator, notStart, safeQuery))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL.getProcessDefinitionKey(), bill.getName(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL.getProcessDefinitionKey(), safeQuery)) {
            employeeRegularBillMapper.selectList(draftWrapper(EmployeeRegularBillDO.class, creator, notStart, safeQuery))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL.getProcessDefinitionKey(), bill.getName(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL.getProcessDefinitionKey(), safeQuery)) {
            employeeTransferBillMapper.selectList(draftWrapper(EmployeeTransferBillDO.class, creator, notStart, safeQuery))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL.getProcessDefinitionKey(), bill.getName(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL.getProcessDefinitionKey(), safeQuery)) {
            employeeResignationBillMapper.selectList(draftWrapper(EmployeeResignationBillDO.class, creator, notStart, safeQuery))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL.getProcessDefinitionKey(), bill.getName(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }

        result.sort(Comparator.comparing(HrmDraftBillRespDTO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    private boolean shouldQuery(String processDefinitionKey, HrmDraftBillQueryDTO query) {
        if (StrUtil.isNotBlank(query.getProcessDefinitionKey())) {
            return processDefinitionKey.equals(query.getProcessDefinitionKey());
        }
        if (CollUtil.isNotEmpty(query.getProcessDefinitionKeys())) {
            return query.getProcessDefinitionKeys().contains(processDefinitionKey);
        }
        return true;
    }

    private <T> LambdaQueryWrapper<T> draftWrapper(Class<T> clazz, String creator, Integer notStart,
                                                   HrmDraftBillQueryDTO query) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>(clazz);
        wrapper.apply("creator = {0}", creator)
                .apply("process_status = {0}", notStart)
                .and(w -> w.apply("process_instance_id IS NULL").or().apply("process_instance_id = ''"));
        if (StrUtil.isNotBlank(query.getBillCode())) {
            wrapper.apply("bill_code LIKE {0}", "%" + query.getBillCode() + "%");
        }
        if (query.getCompanyId() != null) {
            wrapper.apply("company_id = {0}", query.getCompanyId());
        }
        if (query.getDeptId() != null) {
            wrapper.apply("dept_id = {0}", query.getDeptId());
        }
        if (query.getCreateTimeStart() != null) {
            wrapper.apply("create_time >= {0}", query.getCreateTimeStart());
        }
        if (query.getCreateTimeEnd() != null) {
            wrapper.apply("create_time <= {0}", query.getCreateTimeEnd());
        }
        return wrapper;
    }

    private HrmDraftBillRespDTO toDto(Long billId, String billCode, String processDefinitionKey, String summary,
                                      java.time.LocalDateTime createTime, Long companyId, String companyName,
                                      Long deptId, String deptName) {
        HrmDraftBillRespDTO dto = new HrmDraftBillRespDTO();
        dto.setBillId(billId);
        dto.setBillCode(billCode);
        dto.setProcessDefinitionKey(processDefinitionKey);
        dto.setSummary(summary);
        dto.setCreateTime(createTime);
        dto.setCompanyId(companyId);
        dto.setCompanyName(companyName);
        dto.setDeptId(deptId);
        dto.setDeptName(deptName);
        return dto;
    }

}
