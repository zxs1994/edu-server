package cn.dh.edu.module.edu.dal.mysql.reward;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardBudgetYearDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RewardBudgetYearMapper extends BaseMapperX<RewardBudgetYearDO> {

    default List<RewardBudgetYearDO> selectListAll() {
        return selectList(new LambdaQueryWrapperX<RewardBudgetYearDO>()
                .orderByDesc(RewardBudgetYearDO::getBudgetYear));
    }

    default RewardBudgetYearDO selectByBudgetYear(Integer budgetYear) {
        return selectOne(new LambdaQueryWrapperX<RewardBudgetYearDO>()
                .eq(RewardBudgetYearDO::getBudgetYear, budgetYear));
    }

}
