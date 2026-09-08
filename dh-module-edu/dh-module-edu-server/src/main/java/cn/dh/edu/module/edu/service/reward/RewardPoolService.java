package cn.dh.edu.module.edu.service.reward;

import cn.dh.edu.module.edu.controller.admin.reward.vo.*;

import java.util.List;

/**
 * 专项活动预算执行 Service（年度时段预算 + 执行率）
 */
public interface RewardPoolService {

    /**
     * 年度预算列表
     */
    List<RewardBudgetYearRespVO> getBudgetYearList();

    /**
     * 创建年度预算（按模式生成时段）
     */
    Long createBudgetYear(RewardBudgetYearCreateReqVO reqVO);

    /**
     * 更新年度备注；CUSTOM 可整体替换时段
     */
    void updateBudgetYear(RewardBudgetYearUpdateReqVO reqVO);

    /**
     * 更新单段预算（非 CUSTOM 不可改日期）
     */
    void updateBudgetPeriod(RewardBudgetPeriodUpdateReqVO reqVO);

    /**
     * 查询年度执行率
     */
    RewardBudgetExecutionRespVO getBudgetExecution(Integer budgetYear);

    /**
     * 查询时段（或日期范围）预算执行明细（分页）
     */
    RewardBudgetExecutionDetailPageRespVO getBudgetExecutionDetail(RewardBudgetExecutionDetailPageReqVO reqVO);

}
