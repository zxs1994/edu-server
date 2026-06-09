package cn.dh.oa.module.hrm.dal.mysql.employee;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeEducationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工教育经历 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface EmployeeEducationMapper extends BaseMapperX<EmployeeEducationDO> {

    default List<EmployeeEducationDO> selectListByEmployeeId(Long employeeId) {
        return selectList(new LambdaQueryWrapperX<EmployeeEducationDO>()
                .eq(EmployeeEducationDO::getEmployeeId, employeeId)
                .orderByDesc(EmployeeEducationDO::getStartTime));
    }

    default void deleteByEmployeeId(Long employeeId) {
        delete(new LambdaQueryWrapperX<EmployeeEducationDO>()
                .eq(EmployeeEducationDO::getEmployeeId, employeeId));
    }

}

