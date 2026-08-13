package cn.dh.oa.module.hrm.service.bill;

import cn.dh.oa.module.hrm.api.bill.HrmBillExistenceApi;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeEntryBillMapper;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeRegularBillMapper;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeResignationBillMapper;
import cn.dh.oa.module.hrm.dal.mysql.employee.EmployeeTransferBillMapper;
import cn.dh.oa.module.hrm.enums.HrmBillTypeEnum;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 判断 HRM 业务单据是否仍存在
 */
@Service
public class HrmBillExistenceService implements HrmBillExistenceApi {

    @Resource
    private EmployeeEntryBillMapper employeeEntryBillMapper;
    @Resource
    private EmployeeRegularBillMapper employeeRegularBillMapper;
    @Resource
    private EmployeeTransferBillMapper employeeTransferBillMapper;
    @Resource
    private EmployeeResignationBillMapper employeeResignationBillMapper;

    @Override
    public boolean exists(String processDefinitionKey, String businessKey) {
        if (StrUtil.isBlank(businessKey)) {
            return true;
        }
        HrmBillTypeEnum billType = HrmBillTypeEnum.getByProcessDefinitionKey(processDefinitionKey);
        if (billType == null) {
            return true;
        }
        long id;
        try {
            id = Long.parseLong(businessKey);
        } catch (NumberFormatException ex) {
            return true;
        }
        return switch (billType) {
            case HRM_EMPLOYEE_ENTRY_BILL -> employeeEntryBillMapper.selectById(id) != null;
            case HRM_EMPLOYEE_REGULAR_BILL -> employeeRegularBillMapper.selectById(id) != null;
            case HRM_EMPLOYEE_TRANSFER_BILL -> employeeTransferBillMapper.selectById(id) != null;
            case HRM_EMPLOYEE_RESIGNATION_BILL -> employeeResignationBillMapper.selectById(id) != null;
        };
    }

}
