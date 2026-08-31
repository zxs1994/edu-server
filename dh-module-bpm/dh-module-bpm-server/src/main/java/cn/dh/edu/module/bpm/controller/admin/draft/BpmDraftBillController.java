package cn.dh.edu.module.bpm.controller.admin.draft;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.bpm.controller.admin.draft.vo.BpmDraftBillPageReqVO;
import cn.dh.edu.module.bpm.controller.admin.draft.vo.BpmDraftBillRespVO;
import cn.dh.edu.module.bpm.service.draft.BpmDraftBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;
import static cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 草稿箱")
@RestController
@RequestMapping("/bpm/draft-bill")
@Validated
public class BpmDraftBillController {

    @Resource
    private BpmDraftBillService draftBillService;

    @GetMapping("/my-page")
    @Operation(summary = "获得我的草稿箱分页")
    @PreAuthorize("@ss.hasPermission('bpm:process-instance:query')")
    public CommonResult<PageResult<BpmDraftBillRespVO>> getMyDraftBillPage(
            @Valid BpmDraftBillPageReqVO pageReqVO) {
        return success(draftBillService.getMyDraftBillPage(getLoginUserId(), pageReqVO));
    }

}
