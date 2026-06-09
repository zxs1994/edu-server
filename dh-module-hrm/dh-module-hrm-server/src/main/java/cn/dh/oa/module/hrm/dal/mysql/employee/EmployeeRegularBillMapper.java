package cn.dh.oa.module.hrm.dal.mysql.employee;

import java.util.*;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeRegularBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.hrm.controller.admin.employee.vo.EmployeeRegularBillPageReqVO;

/**
 * 员工转正申请单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface EmployeeRegularBillMapper extends BaseMapperX<EmployeeRegularBillDO> {

    default PageResult<EmployeeRegularBillDO> selectPage(EmployeeRegularBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeRegularBillDO>()
                .likeIfPresent(EmployeeRegularBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(EmployeeRegularBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(EmployeeRegularBillDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeRegularBillDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeRegularBillDO::getEmpDeptId, reqVO.getEmpDeptId())
                .likeIfPresent(EmployeeRegularBillDO::getEmpDeptName, reqVO.getEmpDeptName())
                .eqIfPresent(EmployeeRegularBillDO::getEmpCompanyId, reqVO.getEmpCompanyId())
                .likeIfPresent(EmployeeRegularBillDO::getEmpCompanyName, reqVO.getEmpCompanyName())
                .eqIfPresent(EmployeeRegularBillDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .eqIfPresent(EmployeeRegularBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(EmployeeRegularBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeRegularBillDO::getId));
    }

}

