package cn.dh.edu.module.hrm.dal.mysql.employee;

import java.util.*;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.hrm.dal.dataobject.employee.EmployeeTransferBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.edu.module.hrm.controller.admin.employee.vo.EmployeeTransferBillPageReqVO;

/**
 * 人事调动申请单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface EmployeeTransferBillMapper extends BaseMapperX<EmployeeTransferBillDO> {

    default PageResult<EmployeeTransferBillDO> selectPage(EmployeeTransferBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeTransferBillDO>()
                .likeIfPresent(EmployeeTransferBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(EmployeeTransferBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(EmployeeTransferBillDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeTransferBillDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeTransferBillDO::getEmpDeptId, reqVO.getEmpDeptId())
                .likeIfPresent(EmployeeTransferBillDO::getEmpDeptName, reqVO.getEmpDeptName())
                .eqIfPresent(EmployeeTransferBillDO::getEmpCompanyId, reqVO.getEmpCompanyId())
                .likeIfPresent(EmployeeTransferBillDO::getEmpCompanyName, reqVO.getEmpCompanyName())
                .eqIfPresent(EmployeeTransferBillDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .eqIfPresent(EmployeeTransferBillDO::getTransferType, reqVO.getTransferType())
                .eqIfPresent(EmployeeTransferBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(EmployeeTransferBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeTransferBillDO::getId));
    }

}




