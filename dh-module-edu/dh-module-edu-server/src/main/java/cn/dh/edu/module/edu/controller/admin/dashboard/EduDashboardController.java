package cn.dh.edu.module.edu.controller.admin.dashboard;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.edu.controller.admin.dashboard.vo.EduOverviewRespVO;
import cn.dh.edu.module.edu.controller.admin.reward.vo.RewardBudgetExecutionRespVO;
import cn.dh.edu.module.edu.dal.dataobject.reward.RewardBudgetYearDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.reward.RewardBudgetYearMapper;
import cn.dh.edu.module.edu.dal.mysql.student.StudentMapper;
import cn.dh.edu.module.edu.dal.mysql.teacher.TeacherMapper;
import cn.dh.edu.module.edu.enums.reward.RewardBudgetPeriodModeEnum;
import cn.dh.edu.module.edu.service.reward.RewardPoolService;
import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 教培信息总览")
@RestController
@RequestMapping("/edu/dashboard")
@Validated
public class EduDashboardController {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private RewardBudgetYearMapper rewardBudgetYearMapper;
    @Resource
    private RewardPoolService rewardPoolService;

    @GetMapping("/overview")
    @Operation(summary = "获得信息总览")
    @PreAuthorize("@ss.hasPermission('edu:dashboard:overview')")
    public CommonResult<EduOverviewRespVO> getOverview() {
        EduOverviewRespVO respVO = new EduOverviewRespVO();
        respVO.setStudentCount(studentMapper.selectCount());
        respVO.setTeacherCount(teacherMapper.selectCount());
        respVO.setActivityCount(activityMapper.selectCount());
        fillBudgetExec(respVO);
        return success(respVO);
    }

    /**
     * 按当年预算配置回填执行率：季度→当前季，按月→当前月，自定义→覆盖今天的时段（否则全年）
     */
    private void fillBudgetExec(EduOverviewRespVO respVO) {
        int year = LocalDate.now().getYear();
        RewardBudgetYearDO yearDO = rewardBudgetYearMapper.selectByBudgetYear(year);
        if (yearDO == null) {
            return;
        }
        RewardBudgetExecutionRespVO execution = rewardPoolService.getBudgetExecution(year);
        respVO.setBudgetYear(year);
        respVO.setBudgetPeriodMode(execution.getPeriodMode());

        LocalDate today = LocalDate.now();
        List<RewardBudgetExecutionRespVO.PeriodExecution> periods = execution.getPeriods();
        RewardBudgetExecutionRespVO.PeriodExecution current = null;
        if (CollUtil.isNotEmpty(periods)) {
            current = periods.stream()
                    .filter(p -> p.getStartDate() != null && p.getEndDate() != null)
                    .filter(p -> !today.isBefore(p.getStartDate()) && !today.isAfter(p.getEndDate()))
                    .findFirst()
                    .orElse(null);
            // 自定义且今天不落在任一时段：取最近一段（开始日最近）
            if (current == null
                    && RewardBudgetPeriodModeEnum.CUSTOM.getMode().equals(execution.getPeriodMode())) {
                current = periods.stream()
                        .filter(p -> p.getStartDate() != null)
                        .min(Comparator.comparing(
                                (RewardBudgetExecutionRespVO.PeriodExecution p) ->
                                        Math.abs(ChronoUnit.DAYS.between(today, p.getStartDate()))))
                        .orElse(null);
            }
        }

        if (current != null) {
            respVO.setBudgetPeriodName(current.getName());
            respVO.setBudgetExecRate(current.getExecutionRate());
        } else {
            // 兜底全年
            respVO.setBudgetPeriodName("全年");
            respVO.setBudgetExecRate(execution.getExecutionRate());
        }
    }

}
