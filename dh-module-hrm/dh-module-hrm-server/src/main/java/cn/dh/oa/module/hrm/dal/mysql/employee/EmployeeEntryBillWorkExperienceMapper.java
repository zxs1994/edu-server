package cn.dh.oa.module.hrm.dal.mysql.employee;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.hrm.dal.dataobject.employee.EmployeeEntryBillWorkExperienceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工入职申请单工作经历 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface EmployeeEntryBillWorkExperienceMapper extends BaseMapperX<EmployeeEntryBillWorkExperienceDO> {

    default List<EmployeeEntryBillWorkExperienceDO> selectListByEntryBillId(Long entryBillId) {
        return selectList(new LambdaQueryWrapperX<EmployeeEntryBillWorkExperienceDO>()
                .eq(EmployeeEntryBillWorkExperienceDO::getBillId, entryBillId)
                .orderByDesc(EmployeeEntryBillWorkExperienceDO::getStartTime));
    }

    default void deleteByEntryBillId(Long entryBillId) {
        delete(new LambdaQueryWrapperX<EmployeeEntryBillWorkExperienceDO>()
                .eq(EmployeeEntryBillWorkExperienceDO::getBillId, entryBillId));
    }

}


