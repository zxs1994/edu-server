package cn.dh.edu.module.edu.controller.admin.reward;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.edu.controller.admin.reward.vo.*;
import cn.dh.edu.module.edu.service.reward.RewardPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 预算配置")
@RestController
@RequestMapping("/edu/reward-pool")
@Validated
public class RewardPoolController {

    @Resource
    private RewardPoolService rewardPoolService;

    @GetMapping("/budget/year/list")
    @Operation(summary = "获得年度预算列表")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:query')")
    public CommonResult<List<RewardBudgetYearRespVO>> getBudgetYearList() {
        return success(rewardPoolService.getBudgetYearList());
    }

    @PostMapping("/budget/year/create")
    @Operation(summary = "创建年度预算")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:update')")
    public CommonResult<Long> createBudgetYear(@Valid @RequestBody RewardBudgetYearCreateReqVO reqVO) {
        return success(rewardPoolService.createBudgetYear(reqVO));
    }

    @PutMapping("/budget/year/update")
    @Operation(summary = "更新年度预算")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:update')")
    public CommonResult<Boolean> updateBudgetYear(@Valid @RequestBody RewardBudgetYearUpdateReqVO reqVO) {
        rewardPoolService.updateBudgetYear(reqVO);
        return success(true);
    }

    @PutMapping("/budget/period/update")
    @Operation(summary = "更新时段预算")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:update')")
    public CommonResult<Boolean> updateBudgetPeriod(@Valid @RequestBody RewardBudgetPeriodUpdateReqVO reqVO) {
        rewardPoolService.updateBudgetPeriod(reqVO);
        return success(true);
    }

    @GetMapping("/budget/execution")
    @Operation(summary = "获得年度预算执行率")
    @Parameter(name = "budgetYear", description = "预算年度", required = true, example = "2026")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:query')")
    public CommonResult<RewardBudgetExecutionRespVO> getBudgetExecution(
            @RequestParam("budgetYear") @NotNull Integer budgetYear) {
        return success(rewardPoolService.getBudgetExecution(budgetYear));
    }

    @GetMapping("/budget/execution/detail")
    @Operation(summary = "获得预算执行明细（分页）")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:query')")
    public CommonResult<RewardBudgetExecutionDetailPageRespVO> getBudgetExecutionDetail(
            @Valid RewardBudgetExecutionDetailPageReqVO reqVO) {
        return success(rewardPoolService.getBudgetExecutionDetail(reqVO));
    }

}
