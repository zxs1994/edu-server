package cn.dh.oa.module.oa.controller.admin.reception;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.framework.excel.core.util.ExcelUtils;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillPageReqVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillSaveReqVO;
import cn.dh.oa.module.oa.dal.dataobject.reception.ReceptionApplyBillDO;
import cn.dh.oa.module.oa.service.correction.BillCorrectionDisplayEnricher;
import cn.dh.oa.module.oa.service.reception.ReceptionApplyBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.dh.oa.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.dh.oa.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 接待申请单")
@RestController
@RequestMapping("/oa/reception-apply-bill")
@Validated
public class ReceptionApplyBillController {

    @Resource
    private ReceptionApplyBillService receptionApplyBillService;
    @Resource
    private BillCorrectionDisplayEnricher billCorrectionDisplayEnricher;

    @PostMapping("/save")
    @Operation(summary = "保存接待申请单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:reception-apply-bill:create', 'oa:reception-apply-bill:query', 'oa:reception-apply-bill:submit')")
    public CommonResult<Long> saveReceptionApplyBill(@Valid @RequestBody ReceptionApplyBillSaveReqVO saveReqVO) {
        return success(receptionApplyBillService.saveReceptionApplyBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交接待申请单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:reception-apply-bill:create', 'oa:reception-apply-bill:query', 'oa:reception-apply-bill:submit')")
    public CommonResult<Long> submitReceptionApplyBill(@Valid @RequestBody ReceptionApplyBillSaveReqVO submitReqVO) {
        return success(receptionApplyBillService.submitReceptionApplyBill(submitReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除接待申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:reception-apply-bill:delete')")
    public CommonResult<Boolean> deleteReceptionApplyBill(@RequestParam("id") Long id) {
        receptionApplyBillService.deleteReceptionApplyBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除接待申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:reception-apply-bill:delete')")
    public CommonResult<Boolean> deleteReceptionApplyBillList(@RequestParam("ids") @NotEmpty List<Long> ids) {
        receptionApplyBillService.deleteReceptionApplyBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得接待申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('oa:reception-apply-bill:create', 'oa:reception-apply-bill:query', 'oa:reception-apply-bill:submit')")
    public CommonResult<ReceptionApplyBillRespVO> getReceptionApplyBill(@RequestParam("id") Long id) {
        ReceptionApplyBillRespVO respVO = receptionApplyBillService.getReceptionApplyBillInfo(id);
        billCorrectionDisplayEnricher.enrichReceptionApplyBills(List.of(respVO));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得接待申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:reception-apply-bill:query')")
    public CommonResult<PageResult<ReceptionApplyBillRespVO>> getReceptionApplyBillPage(
            @Valid ReceptionApplyBillPageReqVO pageReqVO) {
        PageResult<ReceptionApplyBillDO> pageResult = receptionApplyBillService.getReceptionApplyBillPage(pageReqVO);
        PageResult<ReceptionApplyBillRespVO> voPage = BeanUtils.toBean(pageResult, ReceptionApplyBillRespVO.class);
        billCorrectionDisplayEnricher.enrichReceptionApplyBills(voPage.getList());
        return success(voPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出接待申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:reception-apply-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportReceptionApplyBillExcel(@Valid ReceptionApplyBillPageReqVO pageReqVO,
                                              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReceptionApplyBillDO> list = receptionApplyBillService.getReceptionApplyBillPage(pageReqVO).getList();
        ExcelUtils.write(response, "接待申请单.xls", "数据", ReceptionApplyBillRespVO.class,
                BeanUtils.toBean(list, ReceptionApplyBillRespVO.class));
    }

}
