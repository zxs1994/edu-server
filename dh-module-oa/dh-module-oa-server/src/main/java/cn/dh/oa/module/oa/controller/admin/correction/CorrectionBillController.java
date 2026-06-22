package cn.dh.oa.module.oa.controller.admin.correction;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import static cn.dh.oa.framework.common.pojo.CommonResult.success;

import cn.dh.oa.framework.excel.core.util.ExcelUtils;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import static cn.dh.oa.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.dh.oa.module.oa.controller.admin.correction.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.service.correction.CorrectionBillService;

@Tag(name = "管理后台 - 纠错申请单")
@RestController
@RequestMapping("/oa/correction-bill")
@Validated
public class CorrectionBillController {

    @Resource
    private CorrectionBillService correctionBillService;

    @PostMapping("/save")
    @Operation(summary = "保存纠错申请单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:correction-bill:create', 'oa:correction-bill:query')")
    public CommonResult<Long> saveCorrectionBill(@Valid @RequestBody CorrectionBillSaveReqVO saveReqVO) {
        return success(correctionBillService.saveCorrectionBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交纠错申请单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:correction-bill:create', 'oa:correction-bill:query', 'oa:correction-bill:submit')")
    public CommonResult<Long> submitCorrectionBill(@Valid @RequestBody CorrectionBillSaveReqVO submitReqVO) {
        return success(correctionBillService.submitCorrectionBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新纠错申请单")
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:update')")
    public CommonResult<Boolean> updateCorrectionBill(@Valid @RequestBody CorrectionBillSaveReqVO updateReqVO) {
        correctionBillService.updateCorrectionBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除纠错申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:delete')")
    public CommonResult<Boolean> deleteCorrectionBill(@RequestParam("id") Long id) {
        correctionBillService.deleteCorrectionBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除纠错申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:delete')")
    public CommonResult<Boolean> deleteCorrectionBillList(@RequestParam("ids") List<Long> ids) {
        correctionBillService.deleteCorrectionBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得纠错申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasAnyPermissions('oa:correction-bill:create', 'oa:correction-bill:query')")
    public CommonResult<CorrectionBillRespVO> getCorrectionBill(@RequestParam("id") Long id) {
        CorrectionBillRespVO respVO = correctionBillService.getCorrectionBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得纠错申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:query')")
    public CommonResult<PageResult<CorrectionBillRespVO>> getCorrectionBillPage(@Valid CorrectionBillPageReqVO pageReqVO) {
        PageResult<CorrectionBillDO> pageResult = correctionBillService.getCorrectionBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CorrectionBillRespVO.class));
    }

    @PostMapping("/freeze")
    @Operation(summary = "冻结原单据")
    @Parameter(name = "id", description = "纠错申请单编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:update')")
    public CommonResult<Boolean> freezeCorrectionBill(@RequestParam("id") Long id) {
        correctionBillService.freezeSource(String.valueOf(id));
        return success(true);
    }

    @PostMapping("/unfreeze")
    @Operation(summary = "解冻原单据")
    @Parameter(name = "id", description = "纠错申请单编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:update')")
    public CommonResult<Boolean> unfreezeCorrectionBill(@RequestParam("id") Long id) {
        correctionBillService.unfreezeSource(String.valueOf(id));
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出纠错申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:correction-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCorrectionBillExcel(@Valid CorrectionBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CorrectionBillDO> list = correctionBillService.getCorrectionBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "纠错申请单.xls", "数据", CorrectionBillRespVO.class,
                        BeanUtils.toBean(list, CorrectionBillRespVO.class));
    }

}
