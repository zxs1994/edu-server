package cn.dh.edu.module.hrm.dal.mysql.employee;

import java.util.*;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.hrm.dal.dataobject.employee.EmployeeEntryBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.edu.module.hrm.controller.admin.employee.vo.EmployeeEntryBillPageReqVO;

/**
 * 员工入职申请单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface EmployeeEntryBillMapper extends BaseMapperX<EmployeeEntryBillDO> {

    default PageResult<EmployeeEntryBillDO> selectPage(EmployeeEntryBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeEntryBillDO>()
                .likeIfPresent(EmployeeEntryBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(EmployeeEntryBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(EmployeeEntryBillDO::getName, reqVO.getName())
                .likeIfPresent(EmployeeEntryBillDO::getMobile, reqVO.getMobile())
                .eqIfPresent(EmployeeEntryBillDO::getEmpDeptId, reqVO.getEmpDeptId())
                .likeIfPresent(EmployeeEntryBillDO::getEmpDeptName, reqVO.getEmpDeptName())
                .eqIfPresent(EmployeeEntryBillDO::getEmpCompanyId, reqVO.getEmpCompanyId())
                .likeIfPresent(EmployeeEntryBillDO::getEmpCompanyName, reqVO.getEmpCompanyName())
                .eqIfPresent(EmployeeEntryBillDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .betweenIfPresent(EmployeeEntryBillDO::getEntryDate, reqVO.getEntryDate())
                .eqIfPresent(EmployeeEntryBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(EmployeeEntryBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeEntryBillDO::getId));
    }

}


