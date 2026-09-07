package cn.dh.edu.module.edu.controller.admin.reward;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.edu.controller.admin.reward.vo.*;
import cn.dh.edu.module.edu.service.reward.RewardPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 奖金池")
@RestController
@RequestMapping("/edu/reward-pool")
@Validated
public class RewardPoolController {

    @Resource
    private RewardPoolService rewardPoolService;

    @GetMapping("/get")
    @Operation(summary = "获得系统奖金池")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:query')")
    public CommonResult<RewardPoolRespVO> getRewardPool() {
        return success(rewardPoolService.getRewardPool());
    }

    @PutMapping("/update")
    @Operation(summary = "更新奖金池名称/备注")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:update')")
    public CommonResult<Boolean> updateRewardPool(@Valid @RequestBody RewardPoolUpdateReqVO reqVO) {
        rewardPoolService.updateRewardPool(reqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "启停奖金池")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:update')")
    public CommonResult<Boolean> updateRewardPoolStatus(@Valid @RequestBody RewardPoolUpdateStatusReqVO reqVO) {
        rewardPoolService.updateRewardPoolStatus(reqVO);
        return success(true);
    }

    @PostMapping("/adjust")
    @Operation(summary = "奖金池充值/调减")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:update')")
    public CommonResult<Boolean> adjustRewardPool(@Valid @RequestBody RewardPoolAdjustReqVO reqVO) {
        rewardPoolService.adjustRewardPool(reqVO);
        return success(true);
    }

    @GetMapping("/txn/page")
    @Operation(summary = "获得奖金池流水分页")
    @PreAuthorize("@ss.hasPermission('edu:reward-pool:query')")
    public CommonResult<PageResult<RewardPoolTxnRespVO>> getRewardPoolTxnPage(@Valid RewardPoolTxnPageReqVO pageReqVO) {
        return success(rewardPoolService.getRewardPoolTxnPage(pageReqVO));
    }

}
