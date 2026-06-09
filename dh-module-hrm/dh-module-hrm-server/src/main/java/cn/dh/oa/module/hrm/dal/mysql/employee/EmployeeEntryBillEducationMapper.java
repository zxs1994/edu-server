package cn.dh.oa.module.hrm.dal.mysql.employee;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeEntryBillEducationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工入职申请单教育经历 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface EmployeeEntryBillEducationMapper extends BaseMapperX<EmployeeEntryBillEducationDO> {

    default List<EmployeeEntryBillEducationDO> selectListByEntryBillId(Long entryBillId) {
        return selectList(new LambdaQueryWrapperX<EmployeeEntryBillEducationDO>()
                .eq(EmployeeEntryBillEducationDO::getBillId, entryBillId)
                .orderByDesc(EmployeeEntryBillEducationDO::getStartTime));
    }

    default void deleteByEntryBillId(Long entryBillId) {
        delete(new LambdaQueryWrapperX<EmployeeEntryBillEducationDO>()
                .eq(EmployeeEntryBillEducationDO::getBillId, entryBillId));
    }

}


