package cn.dh.edu.module.edu.service.reward;

import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.reward.vo.*;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardPoolDO;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardPoolTxnDO;
import cn.dh.edu.module.edu.dal.mysql.reward.RewardPoolMapper;
import cn.dh.edu.module.edu.dal.mysql.reward.RewardPoolTxnMapper;
import cn.dh.edu.module.edu.enums.reward.RewardPoolAdjustDirectionEnum;
import cn.dh.edu.module.edu.enums.reward.RewardPoolTxnTypeEnum;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

/**
 * 专项活动奖金池 Service 实现
 */
@Service
@Validated
@Slf4j
public class RewardPoolServiceImpl implements RewardPoolService {

    private static final String DEFAULT_POOL_NAME = "专项活动奖金池";
    private static final String BIZ_TYPE_MANUAL = "MANUAL";

    @Resource
    private RewardPoolMapper rewardPoolMapper;
    @Resource
    private RewardPoolTxnMapper rewardPoolTxnMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RewardPoolRespVO getRewardPool() {
        return toRespVO(getOrCreateDefaultPool());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRewardPool(RewardPoolUpdateReqVO reqVO) {
        RewardPoolDO pool = validatePoolExists(reqVO.getId());
        RewardPoolDO updateObj = new RewardPoolDO();
        updateObj.setId(pool.getId());
        updateObj.setName(reqVO.getName().trim());
        updateObj.setRemark(reqVO.getRemark());
        rewardPoolMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRewardPoolStatus(RewardPoolUpdateStatusReqVO reqVO) {
        validatePoolExists(reqVO.getId());
        if (!Objects.equals(reqVO.getStatus(), CommonStatusEnum.ENABLE.getStatus())
                && !Objects.equals(reqVO.getStatus(), CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(REWARD_POOL_STATUS_INVALID);
        }
        RewardPoolDO updateObj = new RewardPoolDO();
        updateObj.setId(reqVO.getId());
        updateObj.setStatus(reqVO.getStatus());
        rewardPoolMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustRewardPool(RewardPoolAdjustReqVO reqVO) {
        RewardPoolDO pool = getOrCreateDefaultPool();
        validatePoolEnabled(pool);
        BigDecimal amount = reqVO.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(REWARD_POOL_ADJUST_AMOUNT_INVALID);
        }

        String direction = reqVO.getDirection();
        BigDecimal total = nullToZero(pool.getTotalBudget());
        BigDecimal frozen = nullToZero(pool.getFrozenAmount());
        BigDecimal paid = nullToZero(pool.getPaidAmount());
        String txnType;
        BigDecimal newTotal;

        if (RewardPoolAdjustDirectionEnum.UP.getDirection().equals(direction)) {
            txnType = RewardPoolTxnTypeEnum.RECHARGE.getType();
            newTotal = total.add(amount);
        } else if (RewardPoolAdjustDirectionEnum.DOWN.getDirection().equals(direction)) {
            txnType = RewardPoolTxnTypeEnum.ADJUST_DOWN.getType();
            newTotal = total.subtract(amount);
            BigDecimal minTotal = frozen.add(paid);
            if (newTotal.compareTo(minTotal) < 0) {
                throw exception(REWARD_POOL_ADJUST_EXCEED);
            }
        } else {
            throw exception(REWARD_POOL_ADJUST_DIRECTION_INVALID);
        }

        RewardPoolDO updateObj = new RewardPoolDO();
        updateObj.setId(pool.getId());
        updateObj.setTotalBudget(newTotal);
        rewardPoolMapper.updateById(updateObj);

        insertTxn(pool.getId(), txnType, amount, newTotal, frozen, paid,
                BIZ_TYPE_MANUAL, null, reqVO.getRemark().trim());
    }

    @Override
    public PageResult<RewardPoolTxnRespVO> getRewardPoolTxnPage(RewardPoolTxnPageReqVO pageReqVO) {
        if (pageReqVO.getPoolId() == null) {
            RewardPoolDO pool = rewardPoolMapper.selectFirst();
            if (pool != null) {
                pageReqVO.setPoolId(pool.getId());
            }
        }
        PageResult<RewardPoolTxnDO> pageResult = rewardPoolTxnMapper.selectPage(pageReqVO);
        PageResult<RewardPoolTxnRespVO> respPage = BeanUtils.toBean(pageResult, RewardPoolTxnRespVO.class);
        fillCreatorNames(respPage.getList());
        return respPage;
    }

    private void fillCreatorNames(List<RewardPoolTxnRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> userIds = list.stream()
                .map(RewardPoolTxnRespVO::getCreator)
                .filter(StrUtil::isNotBlank)
                .map(creator -> {
                    try {
                        return Long.parseLong(creator);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(userIds)
                ? Collections.emptyMap()
                : adminUserApi.getUserMap(userIds);
        for (RewardPoolTxnRespVO item : list) {
            if (StrUtil.isBlank(item.getCreator())) {
                continue;
            }
            try {
                AdminUserRespDTO user = userMap.get(Long.parseLong(item.getCreator()));
                if (user != null) {
                    item.setCreatorName(StrUtil.blankToDefault(user.getNickname(), user.getUsername()));
                }
            } catch (NumberFormatException ignored) {
                // creator 非数字时保持为空，前端显示 -
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freeze(BigDecimal amount, String bizType, Long bizId, String remark) {
        RewardPoolDO pool = getOrCreateDefaultPool();
        validatePoolEnabled(pool);
        validatePositiveAmount(amount);

        BigDecimal total = nullToZero(pool.getTotalBudget());
        BigDecimal frozen = nullToZero(pool.getFrozenAmount());
        BigDecimal paid = nullToZero(pool.getPaidAmount());
        BigDecimal available = total.subtract(frozen).subtract(paid);
        if (available.compareTo(amount) < 0) {
            throw exception(REWARD_POOL_AVAILABLE_NOT_ENOUGH);
        }
        BigDecimal newFrozen = frozen.add(amount);

        RewardPoolDO updateObj = new RewardPoolDO();
        updateObj.setId(pool.getId());
        updateObj.setFrozenAmount(newFrozen);
        rewardPoolMapper.updateById(updateObj);

        insertTxn(pool.getId(), RewardPoolTxnTypeEnum.FREEZE.getType(), amount,
                total, newFrozen, paid, bizType, bizId, remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfreeze(BigDecimal amount, String bizType, Long bizId, String remark) {
        RewardPoolDO pool = getOrCreateDefaultPool();
        validatePositiveAmount(amount);

        BigDecimal total = nullToZero(pool.getTotalBudget());
        BigDecimal frozen = nullToZero(pool.getFrozenAmount());
        BigDecimal paid = nullToZero(pool.getPaidAmount());
        if (frozen.compareTo(amount) < 0) {
            throw exception(REWARD_POOL_FROZEN_NOT_ENOUGH);
        }
        BigDecimal newFrozen = frozen.subtract(amount);

        RewardPoolDO updateObj = new RewardPoolDO();
        updateObj.setId(pool.getId());
        updateObj.setFrozenAmount(newFrozen);
        rewardPoolMapper.updateById(updateObj);

        insertTxn(pool.getId(), RewardPoolTxnTypeEnum.UNFREEZE.getType(), amount,
                total, newFrozen, paid, bizType, bizId, remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(BigDecimal amount, String bizType, Long bizId, String remark) {
        RewardPoolDO pool = getOrCreateDefaultPool();
        validatePositiveAmount(amount);

        BigDecimal total = nullToZero(pool.getTotalBudget());
        BigDecimal frozen = nullToZero(pool.getFrozenAmount());
        BigDecimal paid = nullToZero(pool.getPaidAmount());
        if (frozen.compareTo(amount) < 0) {
            throw exception(REWARD_POOL_FROZEN_NOT_ENOUGH);
        }
        BigDecimal newFrozen = frozen.subtract(amount);
        BigDecimal newPaid = paid.add(amount);

        RewardPoolDO updateObj = new RewardPoolDO();
        updateObj.setId(pool.getId());
        updateObj.setFrozenAmount(newFrozen);
        updateObj.setPaidAmount(newPaid);
        rewardPoolMapper.updateById(updateObj);

        insertTxn(pool.getId(), RewardPoolTxnTypeEnum.PAY.getType(), amount,
                total, newFrozen, newPaid, bizType, bizId, remark);
    }

    private RewardPoolDO getOrCreateDefaultPool() {
        RewardPoolDO pool = rewardPoolMapper.selectFirst();
        if (pool != null) {
            return pool;
        }
        RewardPoolDO createObj = RewardPoolDO.builder()
                .name(DEFAULT_POOL_NAME)
                .totalBudget(BigDecimal.ZERO)
                .frozenAmount(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .status(CommonStatusEnum.ENABLE.getStatus())
                .remark("系统默认奖金池")
                .build();
        rewardPoolMapper.insert(createObj);
        log.info("[getOrCreateDefaultPool] 创建默认奖金池，id={}", createObj.getId());
        return createObj;
    }

    private RewardPoolDO validatePoolExists(Long id) {
        RewardPoolDO pool = rewardPoolMapper.selectById(id);
        if (pool == null) {
            throw exception(REWARD_POOL_NOT_EXISTS);
        }
        return pool;
    }

    private void validatePoolEnabled(RewardPoolDO pool) {
        if (CommonStatusEnum.isDisable(pool.getStatus())) {
            throw exception(REWARD_POOL_DISABLED);
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(REWARD_POOL_ADJUST_AMOUNT_INVALID);
        }
    }

    private void insertTxn(Long poolId, String txnType, BigDecimal amount,
                           BigDecimal totalAfter, BigDecimal frozenAfter, BigDecimal paidAfter,
                           String bizType, Long bizId, String remark) {
        RewardPoolTxnDO txn = RewardPoolTxnDO.builder()
                .poolId(poolId)
                .txnType(txnType)
                .amount(amount)
                .totalAfter(totalAfter)
                .frozenAfter(frozenAfter)
                .paidAfter(paidAfter)
                .bizType(bizType)
                .bizId(bizId)
                .remark(remark == null ? "" : remark)
                .build();
        rewardPoolTxnMapper.insert(txn);
    }

    private RewardPoolRespVO toRespVO(RewardPoolDO pool) {
        RewardPoolRespVO respVO = BeanUtils.toBean(pool, RewardPoolRespVO.class);
        BigDecimal total = nullToZero(pool.getTotalBudget());
        BigDecimal frozen = nullToZero(pool.getFrozenAmount());
        BigDecimal paid = nullToZero(pool.getPaidAmount());
        respVO.setTotalBudget(total);
        respVO.setFrozenAmount(frozen);
        respVO.setPaidAmount(paid);
        respVO.setAvailableAmount(total.subtract(frozen).subtract(paid));
        return respVO;
    }

    private static BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
