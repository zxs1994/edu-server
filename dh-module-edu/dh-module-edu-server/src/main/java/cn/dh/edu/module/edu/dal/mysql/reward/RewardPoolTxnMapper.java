package cn.dh.edu.module.edu.dal.mysql.reward;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.reward.vo.RewardPoolTxnPageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardPoolTxnDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专项活动奖金池流水 Mapper
 */
@Mapper
public interface RewardPoolTxnMapper extends BaseMapperX<RewardPoolTxnDO> {

    default PageResult<RewardPoolTxnDO> selectPage(RewardPoolTxnPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RewardPoolTxnDO>()
                .eqIfPresent(RewardPoolTxnDO::getPoolId, reqVO.getPoolId())
                .eqIfPresent(RewardPoolTxnDO::getTxnType, reqVO.getTxnType())
                .betweenIfPresent(RewardPoolTxnDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RewardPoolTxnDO::getId));
    }

}
