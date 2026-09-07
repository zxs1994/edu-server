package cn.dh.edu.module.edu.service.reward;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.reward.vo.*;

import java.math.BigDecimal;

/**
 * 专项活动奖金池 Service
 */
public interface RewardPoolService {

    /**
     * 获取系统唯一奖金池；不存在则创建默认池
     */
    RewardPoolRespVO getRewardPool();

    /**
     * 更新池名称、备注
     */
    void updateRewardPool(RewardPoolUpdateReqVO reqVO);

    /**
     * 启停
     */
    void updateRewardPoolStatus(RewardPoolUpdateStatusReqVO reqVO);

    /**
     * 充值 / 调减总额
     */
    void adjustRewardPool(RewardPoolAdjustReqVO reqVO);

    /**
     * 流水分页
     */
    PageResult<RewardPoolTxnRespVO> getRewardPoolTxnPage(RewardPoolTxnPageReqVO pageReqVO);

    /**
     * 预留：活动审批通过时冻结预算
     */
    void freeze(BigDecimal amount, String bizType, Long bizId, String remark);

    /**
     * 预留：活动取消/驳回时解冻
     */
    void unfreeze(BigDecimal amount, String bizType, Long bizId, String remark);

    /**
     * 预留：付款完成时扣减冻结并计入实发
     */
    void pay(BigDecimal amount, String bizType, Long bizId, String remark);

}
