package cn.dh.edu.module.edu.dal.mysql.reward;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardPoolDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 专项活动奖金池 Mapper
 */
@Mapper
public interface RewardPoolMapper extends BaseMapperX<RewardPoolDO> {

    default RewardPoolDO selectFirst() {
        List<RewardPoolDO> list = selectList(new LambdaQueryWrapperX<RewardPoolDO>()
                .orderByAsc(RewardPoolDO::getId)
                .last("LIMIT 1"));
        return list.isEmpty() ? null : list.get(0);
    }

}
