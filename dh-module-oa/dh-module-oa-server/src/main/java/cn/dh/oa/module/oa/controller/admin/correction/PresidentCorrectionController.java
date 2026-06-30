package cn.dh.oa.module.oa.controller.admin.correction;

import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.correction.vo.PresidentCorrectionInitiateReqVO;
import cn.dh.oa.module.oa.controller.admin.correction.vo.BillCorrectionHistoryRespVO;
import cn.dh.oa.module.oa.service.correction.PresidentCorrectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.oa.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会长纠错管理")
@RestController
@RequestMapping("/oa/president-correction")
@Validated
public class PresidentCorrectionController {

    @Resource
    private PresidentCorrectionService presidentCorrectionService;

    @PostMapping("/initiate")
    @Operation(summary = "会长发起纠错")
    @PreAuthorize("@ss.hasRole('cpha_chairman') and @ss.hasPermission('oa:president-correction:initiate')")
    public CommonResult<Long> initiate(@Valid @RequestBody PresidentCorrectionInitiateReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(presidentCorrectionService.initiate(userId, reqVO));
    }

    @GetMapping("/bill-history")
    @Operation(summary = "业务单据纠错历史（审批页展示原审批与撤销记录）")
    public CommonResult<BillCorrectionHistoryRespVO> getBillHistory(
            @RequestParam("sourceBillType") String sourceBillType,
            @RequestParam("sourceBillId") Long sourceBillId) {
        return success(presidentCorrectionService.getBillHistory(sourceBillType, sourceBillId));
    }

}
