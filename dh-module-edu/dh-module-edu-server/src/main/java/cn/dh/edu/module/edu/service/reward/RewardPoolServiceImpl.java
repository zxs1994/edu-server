package cn.dh.edu.module.edu.service.reward;

import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.edu.module.edu.controller.admin.reward.vo.*;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityPaymentRequestDO;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardBudgetPeriodDO;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardBudgetYearDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityPaymentRequestMapper;
import cn.dh.edu.module.edu.dal.mysql.reward.RewardBudgetPeriodMapper;
import cn.dh.edu.module.edu.dal.mysql.reward.RewardBudgetYearMapper;
import cn.dh.edu.module.edu.enums.reward.RewardBudgetPeriodModeEnum;
import cn.dh.edu.module.edu.service.activity.EduExchangeRateService;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

/**
 * 专项活动预算配置 Service 实现
 */
@Service
@Validated
@Slf4j
public class RewardPoolServiceImpl implements RewardPoolService {

    @Resource
    private RewardBudgetYearMapper rewardBudgetYearMapper;
    @Resource
    private RewardBudgetPeriodMapper rewardBudgetPeriodMapper;
    @Resource
    private ActivityPaymentRequestMapper activityPaymentRequestMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private EduExchangeRateService exchangeRateService;

    @Override
    public List<RewardBudgetYearRespVO> getBudgetYearList() {
        List<RewardBudgetYearDO> years = rewardBudgetYearMapper.selectListAll();
        if (CollUtil.isEmpty(years)) {
            return Collections.emptyList();
        }
        Map<Long, List<RewardBudgetPeriodDO>> periodMap = rewardBudgetPeriodMapper
                .selectListByYearIds(years.stream().map(RewardBudgetYearDO::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(RewardBudgetPeriodDO::getYearId));
        List<RewardBudgetYearRespVO> result = new ArrayList<>(years.size());
        for (RewardBudgetYearDO year : years) {
            result.add(toYearRespVO(year, periodMap.getOrDefault(year.getId(), Collections.emptyList())));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBudgetYear(RewardBudgetYearCreateReqVO reqVO) {
        validateBudgetYear(reqVO.getBudgetYear());
        RewardBudgetPeriodModeEnum mode = RewardBudgetPeriodModeEnum.of(reqVO.getPeriodMode());
        if (mode == null) {
            throw exception(REWARD_BUDGET_PERIOD_MODE_INVALID);
        }
        if (rewardBudgetYearMapper.selectByBudgetYear(reqVO.getBudgetYear()) != null) {
            throw exception(REWARD_BUDGET_YEAR_EXISTS);
        }

        List<PeriodDraft> drafts = buildPeriodDrafts(mode, reqVO.getBudgetYear(), reqVO.getPeriods());
        if (mode == RewardBudgetPeriodModeEnum.QUARTER || mode == RewardBudgetPeriodModeEnum.MONTH) {
            BigDecimal total = nullToZero(reqVO.getTotalBudget());
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            drafts = distributeTotalEvenly(drafts, total);
        }
        validatePeriodDrafts(drafts);

        RewardBudgetYearDO year = RewardBudgetYearDO.builder()
                .budgetYear(reqVO.getBudgetYear())
                .periodMode(mode.getMode())
                .totalBudget(sumBudget(drafts))
                .remark(reqVO.getRemark())
                .build();
        rewardBudgetYearMapper.insert(year);
        insertPeriods(year.getId(), drafts);
        return year.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetYear(RewardBudgetYearUpdateReqVO reqVO) {
        RewardBudgetYearDO year = validateBudgetYearExists(reqVO.getId());
        RewardBudgetYearDO updateObj = new RewardBudgetYearDO();
        updateObj.setId(year.getId());
        updateObj.setRemark(reqVO.getRemark());

        String effectiveMode = year.getPeriodMode();
        if (StrUtil.isNotBlank(reqVO.getPeriodMode())
                && !Objects.equals(reqVO.getPeriodMode(), year.getPeriodMode())) {
            RewardBudgetPeriodModeEnum newMode = RewardBudgetPeriodModeEnum.of(reqVO.getPeriodMode());
            if (newMode == null) {
                throw exception(REWARD_BUDGET_PERIOD_MODE_INVALID);
            }
            effectiveMode = newMode.getMode();
            updateObj.setPeriodMode(effectiveMode);
            if (newMode != RewardBudgetPeriodModeEnum.CUSTOM) {
                // 切到季度/月：按新模式重建时段，原全年预算均分到各段
                BigDecimal oldTotal = nullToZero(year.getTotalBudget());
                List<PeriodDraft> drafts = buildPeriodDrafts(newMode, year.getBudgetYear(), null);
                drafts = distributeTotalEvenly(drafts, oldTotal);
                rewardBudgetPeriodMapper.deleteByYearId(year.getId());
                insertPeriods(year.getId(), drafts);
                updateObj.setTotalBudget(sumBudget(drafts));
            }
            // 切到自定义：保留现有时段，仅改模式，日期/金额可继续编辑
        }

        if (reqVO.getPeriods() != null) {
            if (!RewardBudgetPeriodModeEnum.CUSTOM.getMode().equals(effectiveMode)) {
                throw exception(REWARD_BUDGET_PERIOD_NOT_EDITABLE);
            }
            List<PeriodDraft> drafts = toPeriodDraftsFromUpdate(reqVO.getPeriods());
            validatePeriodDrafts(drafts);
            rewardBudgetPeriodMapper.deleteByYearId(year.getId());
            insertPeriods(year.getId(), drafts);
            updateObj.setTotalBudget(sumBudget(drafts));
        }

        // 重置全年预算并均分到当前各时段（保留时段结构）
        if (reqVO.getTotalBudget() != null) {
            BigDecimal total = nullToZero(reqVO.getTotalBudget());
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            List<RewardBudgetPeriodDO> periods = rewardBudgetPeriodMapper.selectListByYearId(year.getId());
            if (CollUtil.isEmpty(periods)) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            List<PeriodDraft> drafts = periods.stream()
                    .sorted(Comparator.comparing(RewardBudgetPeriodDO::getPeriodNo,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(p -> new PeriodDraft(
                            p.getName(), p.getPeriodNo(), p.getStartDate(), p.getEndDate(), BigDecimal.ZERO))
                    .collect(Collectors.toCollection(ArrayList::new));
            drafts = distributeTotalEvenly(drafts, total);
            // total=0 时 distribute 原样返回，需显式清零
            if (total.compareTo(BigDecimal.ZERO) == 0) {
                drafts = drafts.stream()
                        .map(d -> new PeriodDraft(d.name(), d.periodNo(), d.startDate(), d.endDate(), BigDecimal.ZERO))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
            rewardBudgetPeriodMapper.deleteByYearId(year.getId());
            insertPeriods(year.getId(), drafts);
            updateObj.setTotalBudget(sumBudget(drafts));
        }
        rewardBudgetYearMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetPeriod(RewardBudgetPeriodUpdateReqVO reqVO) {
        RewardBudgetPeriodDO period = rewardBudgetPeriodMapper.selectById(reqVO.getId());
        if (period == null) {
            throw exception(REWARD_BUDGET_PERIOD_NOT_EXISTS);
        }
        RewardBudgetYearDO year = validateBudgetYearExists(period.getYearId());
        boolean custom = RewardBudgetPeriodModeEnum.CUSTOM.getMode().equals(year.getPeriodMode());

        BigDecimal amount = nullToZero(reqVO.getBudgetAmount());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw exception(REWARD_BUDGET_PERIOD_INVALID);
        }
        String name = StrUtil.trim(reqVO.getName());
        if (StrUtil.isBlank(name)) {
            throw exception(REWARD_BUDGET_PERIOD_INVALID);
        }

        LocalDate startDate = period.getStartDate();
        LocalDate endDate = period.getEndDate();
        if (custom) {
            if (reqVO.getStartDate() == null || reqVO.getEndDate() == null) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            startDate = reqVO.getStartDate();
            endDate = reqVO.getEndDate();
            if (endDate.isBefore(startDate)) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
        } else if (reqVO.getStartDate() != null || reqVO.getEndDate() != null) {
            throw exception(REWARD_BUDGET_PERIOD_NOT_EDITABLE);
        }

        List<RewardBudgetPeriodDO> others = rewardBudgetPeriodMapper.selectListByYearId(year.getId()).stream()
                .filter(item -> !Objects.equals(item.getId(), period.getId()))
                .toList();
        for (RewardBudgetPeriodDO other : others) {
            if (datesOverlap(startDate, endDate, other.getStartDate(), other.getEndDate())) {
                throw exception(REWARD_BUDGET_PERIOD_OVERLAP);
            }
        }

        RewardBudgetPeriodDO updateObj = new RewardBudgetPeriodDO();
        updateObj.setId(period.getId());
        updateObj.setName(name);
        updateObj.setBudgetAmount(amount);
        updateObj.setStartDate(startDate);
        updateObj.setEndDate(endDate);
        rewardBudgetPeriodMapper.updateById(updateObj);

        refreshYearTotalBudget(year.getId());
    }

    @Override
    public RewardBudgetExecutionRespVO getBudgetExecution(Integer budgetYear) {
        validateBudgetYear(budgetYear);
        RewardBudgetYearDO year = rewardBudgetYearMapper.selectByBudgetYear(budgetYear);
        if (year == null) {
            throw exception(REWARD_BUDGET_YEAR_NOT_EXISTS);
        }
        List<RewardBudgetPeriodDO> periods = rewardBudgetPeriodMapper.selectListByYearId(year.getId());

        LocalDateTime rangeStart = LocalDate.of(budgetYear, 1, 1).atStartOfDay();
        LocalDateTime rangeEnd = LocalDate.of(budgetYear, 12, 31).atTime(LocalTime.MAX);
        List<ActivityPaymentRequestDO> payments = activityPaymentRequestMapper
                .selectApprovedByApproveTimeBetween(rangeStart, rangeEnd);
        payments = payments.stream()
                .filter(p -> Objects.equals(p.getProcessStatus(), BpmProcessInstanceStatusEnum.APPROVE.getStatus()))
                .filter(p -> p.getApproveTime() != null)
                .toList();

        Set<String> currencies = payments.stream()
                .filter(p -> p.getAmountCny() == null)
                .map(ActivityPaymentRequestDO::getCurrency)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        exchangeRateService.prefetchCnyRates(currencies);

        BigDecimal totalBudget = nullToZero(year.getTotalBudget());
        BigDecimal totalExecuted = BigDecimal.ZERO;
        List<RewardBudgetExecutionRespVO.PeriodExecution> periodExecutions = new ArrayList<>();
        for (RewardBudgetPeriodDO period : periods) {
            BigDecimal executed = BigDecimal.ZERO;
            LocalDateTime pStart = period.getStartDate().atStartOfDay();
            LocalDateTime pEnd = period.getEndDate().atTime(LocalTime.MAX);
            for (ActivityPaymentRequestDO payment : payments) {
                LocalDateTime approveTime = payment.getApproveTime();
                if (approveTime.isBefore(pStart) || approveTime.isAfter(pEnd)) {
                    continue;
                }
                executed = executed.add(resolveLockedAmountCny(payment));
            }
            totalExecuted = totalExecuted.add(executed);
            RewardBudgetExecutionRespVO.PeriodExecution item = new RewardBudgetExecutionRespVO.PeriodExecution();
            item.setId(period.getId());
            item.setName(period.getName());
            item.setPeriodNo(period.getPeriodNo());
            item.setStartDate(period.getStartDate());
            item.setEndDate(period.getEndDate());
            item.setBudgetAmount(nullToZero(period.getBudgetAmount()));
            item.setExecutedAmount(executed);
            item.setExecutionRate(calcRate(executed, period.getBudgetAmount()));
            periodExecutions.add(item);
        }

        RewardBudgetExecutionRespVO respVO = new RewardBudgetExecutionRespVO();
        respVO.setYearId(year.getId());
        respVO.setBudgetYear(year.getBudgetYear());
        respVO.setPeriodMode(year.getPeriodMode());
        respVO.setTotalBudget(totalBudget);
        respVO.setTotalExecuted(totalExecuted);
        respVO.setExecutionRate(calcRate(totalExecuted, totalBudget));
        respVO.setPeriods(periodExecutions);
        return respVO;
    }

    @Override
    public RewardBudgetExecutionDetailPageRespVO getBudgetExecutionDetail(
            RewardBudgetExecutionDetailPageReqVO reqVO) {
        validateBudgetYear(reqVO.getBudgetYear());
        RewardBudgetYearDO year = rewardBudgetYearMapper.selectByBudgetYear(reqVO.getBudgetYear());
        if (year == null) {
            throw exception(REWARD_BUDGET_YEAR_NOT_EXISTS);
        }

        LocalDate startDate;
        LocalDate endDate;
        Long periodId = reqVO.getPeriodId();
        String periodName = null;
        if (periodId != null) {
            RewardBudgetPeriodDO period = rewardBudgetPeriodMapper.selectById(periodId);
            if (period == null || !Objects.equals(period.getYearId(), year.getId())) {
                throw exception(REWARD_BUDGET_PERIOD_NOT_EXISTS);
            }
            startDate = period.getStartDate();
            endDate = period.getEndDate();
            periodName = period.getName();
        } else if (reqVO.getStartDate() != null && reqVO.getEndDate() != null) {
            startDate = reqVO.getStartDate();
            endDate = reqVO.getEndDate();
            if (endDate.isBefore(startDate)) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            periodName = startDate + " ~ " + endDate;
        } else {
            throw exception(REWARD_BUDGET_PERIOD_INVALID);
        }

        LocalDateTime rangeStart = startDate.atStartOfDay();
        LocalDateTime rangeEnd = endDate.atTime(LocalTime.MAX);
        List<ActivityPaymentRequestDO> payments = activityPaymentRequestMapper
                .selectApprovedByApproveTimeBetween(rangeStart, rangeEnd)
                .stream()
                .filter(p -> Objects.equals(p.getProcessStatus(), BpmProcessInstanceStatusEnum.APPROVE.getStatus()))
                .filter(p -> p.getApproveTime() != null)
                .sorted(Comparator.comparing(ActivityPaymentRequestDO::getApproveTime).reversed())
                .toList();

        Set<String> currencies = payments.stream()
                .filter(p -> p.getAmountCny() == null)
                .map(ActivityPaymentRequestDO::getCurrency)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        exchangeRateService.prefetchCnyRates(currencies);

        Set<Long> activityIds = payments.stream()
                .map(ActivityPaymentRequestDO::getActivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityDO> activityMap = CollUtil.isEmpty(activityIds)
                ? Map.of()
                : activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityDO::getId, a -> a, (a, b) -> a));

        Set<Long> applicantIds = payments.stream()
                .map(ActivityPaymentRequestDO::getApplicantUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(applicantIds)
                ? Map.of()
                : adminUserApi.getUserMap(applicantIds);

        List<RewardBudgetExecutionDetailPageRespVO.Item> allItems = new ArrayList<>(payments.size());
        BigDecimal executedAmountCny = BigDecimal.ZERO;
        for (ActivityPaymentRequestDO payment : payments) {
            BigDecimal amountCny = resolveLockedAmountCny(payment);
            executedAmountCny = executedAmountCny.add(amountCny);

            RewardBudgetExecutionDetailPageRespVO.Item item = new RewardBudgetExecutionDetailPageRespVO.Item();
            item.setPaymentRequestId(payment.getId());
            item.setBillCode(payment.getBillCode());
            item.setTitle(payment.getTitle());
            item.setActivityId(payment.getActivityId());
            ActivityDO activity = payment.getActivityId() == null ? null : activityMap.get(payment.getActivityId());
            item.setActivityName(activity != null ? activity.getName() : null);
            item.setApplicantUserId(payment.getApplicantUserId());
            AdminUserRespDTO user = payment.getApplicantUserId() == null
                    ? null : userMap.get(payment.getApplicantUserId());
            item.setApplicantUserName(user != null ? user.getNickname() : null);
            item.setApproveTime(payment.getApproveTime());
            item.setTotalAmount(nullToZero(payment.getTotalAmount()));
            item.setCurrency(payment.getCurrency());
            item.setExchangeRate(payment.getExchangeRate());
            item.setAmountCny(amountCny);
            allItems.add(item);
        }

        int pageNo = reqVO.getPageNo() == null || reqVO.getPageNo() < 1 ? 1 : reqVO.getPageNo();
        int pageSize = reqVO.getPageSize() == null || reqVO.getPageSize() < 1 ? 10 : reqVO.getPageSize();
        int from = Math.min((pageNo - 1) * pageSize, allItems.size());
        int to = Math.min(from + pageSize, allItems.size());

        RewardBudgetExecutionDetailPageRespVO respVO = new RewardBudgetExecutionDetailPageRespVO();
        respVO.setBudgetYear(reqVO.getBudgetYear());
        respVO.setPeriodId(periodId);
        respVO.setPeriodName(periodName);
        respVO.setStartDate(startDate);
        respVO.setEndDate(endDate);
        respVO.setExecutedAmountCny(executedAmountCny);
        respVO.setTotal((long) allItems.size());
        respVO.setList(allItems.subList(from, to));
        return respVO;
    }

    private List<PeriodDraft> buildPeriodDrafts(RewardBudgetPeriodModeEnum mode, Integer budgetYear,
                                                List<RewardBudgetYearCreateReqVO.PeriodItem> customPeriods) {
        if (mode == RewardBudgetPeriodModeEnum.QUARTER) {
            List<PeriodDraft> drafts = new ArrayList<>(4);
            String[] names = {"Q1", "Q2", "Q3", "Q4"};
            int[][] months = {{1, 3}, {4, 6}, {7, 9}, {10, 12}};
            for (int i = 0; i < 4; i++) {
                LocalDate start = LocalDate.of(budgetYear, months[i][0], 1);
                LocalDate end = YearMonth.of(budgetYear, months[i][1]).atEndOfMonth();
                drafts.add(new PeriodDraft(names[i], i + 1, start, end, BigDecimal.ZERO));
            }
            return drafts;
        }
        if (mode == RewardBudgetPeriodModeEnum.MONTH) {
            List<PeriodDraft> drafts = new ArrayList<>(12);
            for (int month = 1; month <= 12; month++) {
                YearMonth ym = YearMonth.of(budgetYear, month);
                drafts.add(new PeriodDraft(month + "月", month, ym.atDay(1), ym.atEndOfMonth(), BigDecimal.ZERO));
            }
            return drafts;
        }
        // CUSTOM：创建时带一段默认全年，便于直接编辑
        if (CollUtil.isEmpty(customPeriods)) {
            LocalDate start = LocalDate.of(budgetYear, 1, 1);
            LocalDate end = LocalDate.of(budgetYear, 12, 31);
            return List.of(new PeriodDraft("自定义时段", 1, start, end, BigDecimal.ZERO));
        }
        return toPeriodDraftsFromCreate(customPeriods);
    }

    private List<PeriodDraft> toPeriodDraftsFromCreate(List<RewardBudgetYearCreateReqVO.PeriodItem> items) {
        List<PeriodDraft> drafts = new ArrayList<>();
        int no = 1;
        for (RewardBudgetYearCreateReqVO.PeriodItem item : items) {
            drafts.add(new PeriodDraft(
                    StrUtil.trim(item.getName()),
                    no++,
                    item.getStartDate(),
                    item.getEndDate(),
                    nullToZero(item.getBudgetAmount())));
        }
        return drafts;
    }

    private List<PeriodDraft> toPeriodDraftsFromUpdate(List<RewardBudgetYearUpdateReqVO.PeriodItem> items) {
        List<PeriodDraft> drafts = new ArrayList<>();
        int no = 1;
        for (RewardBudgetYearUpdateReqVO.PeriodItem item : items) {
            drafts.add(new PeriodDraft(
                    StrUtil.trim(item.getName()),
                    no++,
                    item.getStartDate(),
                    item.getEndDate(),
                    nullToZero(item.getBudgetAmount())));
        }
        return drafts;
    }

    private void validatePeriodDrafts(List<PeriodDraft> drafts) {
        if (CollUtil.isEmpty(drafts)) {
            return;
        }
        for (PeriodDraft draft : drafts) {
            if (StrUtil.isBlank(draft.name()) || draft.startDate() == null || draft.endDate() == null) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            if (draft.endDate().isBefore(draft.startDate())) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
            if (draft.budgetAmount() == null || draft.budgetAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw exception(REWARD_BUDGET_PERIOD_INVALID);
            }
        }
        List<PeriodDraft> sorted = drafts.stream()
                .sorted(Comparator.comparing(PeriodDraft::startDate).thenComparing(PeriodDraft::endDate))
                .toList();
        for (int i = 1; i < sorted.size(); i++) {
            PeriodDraft prev = sorted.get(i - 1);
            PeriodDraft curr = sorted.get(i);
            if (datesOverlap(prev.startDate(), prev.endDate(), curr.startDate(), curr.endDate())) {
                throw exception(REWARD_BUDGET_PERIOD_OVERLAP);
            }
        }
    }

    private boolean datesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }

    private void insertPeriods(Long yearId, List<PeriodDraft> drafts) {
        for (PeriodDraft draft : drafts) {
            RewardBudgetPeriodDO period = RewardBudgetPeriodDO.builder()
                    .yearId(yearId)
                    .name(draft.name())
                    .periodNo(draft.periodNo())
                    .startDate(draft.startDate())
                    .endDate(draft.endDate())
                    .budgetAmount(draft.budgetAmount())
                    .build();
            rewardBudgetPeriodMapper.insert(period);
        }
    }

    private void refreshYearTotalBudget(Long yearId) {
        List<RewardBudgetPeriodDO> periods = rewardBudgetPeriodMapper.selectListByYearId(yearId);
        BigDecimal total = periods.stream()
                .map(RewardBudgetPeriodDO::getBudgetAmount)
                .map(RewardPoolServiceImpl::nullToZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        RewardBudgetYearDO updateObj = new RewardBudgetYearDO();
        updateObj.setId(yearId);
        updateObj.setTotalBudget(total);
        rewardBudgetYearMapper.updateById(updateObj);
    }

    private BigDecimal sumBudget(List<PeriodDraft> drafts) {
        return drafts.stream()
                .map(PeriodDraft::budgetAmount)
                .map(RewardPoolServiceImpl::nullToZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 将总额均分到各时段（余数落在最后一段） */
    private List<PeriodDraft> distributeTotalEvenly(List<PeriodDraft> drafts, BigDecimal total) {
        if (CollUtil.isEmpty(drafts) || total.compareTo(BigDecimal.ZERO) <= 0) {
            return drafts;
        }
        int size = drafts.size();
        BigDecimal unit = total.divide(BigDecimal.valueOf(size), 2, RoundingMode.DOWN);
        BigDecimal assigned = BigDecimal.ZERO;
        List<PeriodDraft> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            PeriodDraft draft = drafts.get(i);
            BigDecimal amount = (i == size - 1) ? total.subtract(assigned) : unit;
            assigned = assigned.add(amount);
            result.add(new PeriodDraft(draft.name(), draft.periodNo(), draft.startDate(), draft.endDate(), amount));
        }
        return result;
    }

    private BigDecimal calcRate(BigDecimal executed, BigDecimal budget) {
        BigDecimal budgetVal = nullToZero(budget);
        if (budgetVal.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return nullToZero(executed).divide(budgetVal, 4, RoundingMode.HALF_UP);
    }

    /**
     * 优先使用审批时锁定的折合人民币；历史未锁定单据首次汇总时补锁当前汇率
     */
    private BigDecimal resolveLockedAmountCny(ActivityPaymentRequestDO payment) {
        if (payment.getAmountCny() != null) {
            return payment.getAmountCny();
        }
        BigDecimal rate = exchangeRateService.getCnyRate(payment.getCurrency());
        BigDecimal amountCny = exchangeRateService.toCny(
                nullToZero(payment.getTotalAmount()), payment.getCurrency());
        ActivityPaymentRequestDO lockUpdate = new ActivityPaymentRequestDO();
        lockUpdate.setId(payment.getId());
        lockUpdate.setExchangeRate(rate);
        lockUpdate.setAmountCny(amountCny);
        activityPaymentRequestMapper.updateById(lockUpdate);
        payment.setExchangeRate(rate);
        payment.setAmountCny(amountCny);
        return amountCny;
    }

    private RewardBudgetYearRespVO toYearRespVO(RewardBudgetYearDO year, List<RewardBudgetPeriodDO> periods) {
        RewardBudgetYearRespVO respVO = BeanUtils.toBean(year, RewardBudgetYearRespVO.class);
        List<RewardBudgetYearRespVO.PeriodItem> items = periods.stream().map(period -> {
            RewardBudgetYearRespVO.PeriodItem item = new RewardBudgetYearRespVO.PeriodItem();
            item.setId(period.getId());
            item.setName(period.getName());
            item.setPeriodNo(period.getPeriodNo());
            item.setStartDate(period.getStartDate());
            item.setEndDate(period.getEndDate());
            item.setBudgetAmount(nullToZero(period.getBudgetAmount()));
            return item;
        }).toList();
        respVO.setPeriods(items);
        return respVO;
    }

    private void validateBudgetYear(Integer budgetYear) {
        if (budgetYear == null || budgetYear < 2000 || budgetYear > 2100) {
            throw exception(REWARD_BUDGET_YEAR_INVALID);
        }
    }

    private RewardBudgetYearDO validateBudgetYearExists(Long id) {
        RewardBudgetYearDO year = rewardBudgetYearMapper.selectById(id);
        if (year == null) {
            throw exception(REWARD_BUDGET_YEAR_NOT_EXISTS);
        }
        return year;
    }

    private static BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record PeriodDraft(String name, Integer periodNo, LocalDate startDate, LocalDate endDate,
                               BigDecimal budgetAmount) {
    }

}
