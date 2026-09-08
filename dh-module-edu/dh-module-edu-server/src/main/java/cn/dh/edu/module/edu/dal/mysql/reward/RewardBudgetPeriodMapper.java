package cn.dh.edu.module.edu.dal.mysql.reward;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardBudgetPeriodDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RewardBudgetPeriodMapper extends BaseMapperX<RewardBudgetPeriodDO> {

    default List<RewardBudgetPeriodDO> selectListByYearId(Long yearId) {
        return selectList(new LambdaQueryWrapperX<RewardBudgetPeriodDO>()
                .eq(RewardBudgetPeriodDO::getYearId, yearId)
                .orderByAsc(RewardBudgetPeriodDO::getPeriodNo)
                .orderByAsc(RewardBudgetPeriodDO::getStartDate));
    }

    default void deleteByYearId(Long yearId) {
        delete(new LambdaQueryWrapperX<RewardBudgetPeriodDO>()
                .eq(RewardBudgetPeriodDO::getYearId, yearId));
    }

    default List<RewardBudgetPeriodDO> selectListByYearIds(Collection<Long> yearIds) {
        return selectList(new LambdaQueryWrapperX<RewardBudgetPeriodDO>()
                .in(RewardBudgetPeriodDO::getYearId, yearIds)
                .orderByAsc(RewardBudgetPeriodDO::getPeriodNo)
                .orderByAsc(RewardBudgetPeriodDO::getStartDate));
    }

}
