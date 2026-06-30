package cn.dh.oa.module.oa.controller.admin.incoming;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.framework.excel.core.util.ExcelUtils;
import cn.dh.oa.module.oa.controller.admin.incoming.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.incoming.IncomingDocumentBillDO;
import cn.dh.oa.module.oa.service.incoming.IncomingDocumentBillService;
import cn.dh.oa.module.oa.service.correction.BillCorrectionDisplayEnricher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.dh.oa.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.dh.oa.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 收文办理单")
@RestController
@RequestMapping("/oa/incoming-document-bill")
@Validated
public class IncomingDocumentBillController {

    @Resource
    private IncomingDocumentBillService incomingDocumentBillService;
    @Resource
    private BillCorrectionDisplayEnricher billCorrectionDisplayEnricher;

    @PostMapping("/create")
    @Operation(summary = "创建收文办理单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:incoming-document-bill:create', 'oa:incoming-document-bill:query', 'oa:incoming-document-bill:submit')")
    public CommonResult<Long> createIncomingDocumentBill(@Valid @RequestBody IncomingDocumentBillSaveReqVO createReqVO) {
        return success(incomingDocumentBillService.createIncomingDocumentBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存收文办理单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:incoming-document-bill:create', 'oa:incoming-document-bill:query', 'oa:incoming-document-bill:submit')")
    public CommonResult<Long> saveIncomingDocumentBill(@Valid @RequestBody IncomingDocumentBillSaveReqVO saveReqVO) {
        return success(incomingDocumentBillService.saveIncomingDocumentBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交收文办理单")
    @PreAuthorize("@ss.hasAnyPermissions('oa:incoming-document-bill:create', 'oa:incoming-document-bill:query', 'oa:incoming-document-bill:submit')")
    public CommonResult<Long> submitIncomingDocumentBill(@Valid @RequestBody IncomingDocumentBillSaveReqVO submitReqVO) {
        return success(incomingDocumentBillService.submitIncomingDocumentBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新收文办理单")
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:update')")
    public CommonResult<Boolean> updateIncomingDocumentBill(@Valid @RequestBody IncomingDocumentBillSaveReqVO updateReqVO) {
        incomingDocumentBillService.updateIncomingDocumentBill(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-handling-status")
    @Operation(summary = "更新办理状态")
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:update')")
    public CommonResult<Boolean> updateHandlingStatus(@RequestParam("id") Long id,
                                                       @RequestParam("handlingStatus") Integer handlingStatus) {
        incomingDocumentBillService.updateHandlingStatus(id, handlingStatus);
        return success(true);
    }

    @PutMapping("/update-handling-result")
    @Operation(summary = "更新办理结果")
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:update')")
    public CommonResult<Boolean> updateHandlingResult(@RequestParam("id") Long id,
                                                       @RequestParam("handlingResult") String handlingResult) {
        incomingDocumentBillService.updateHandlingResult(id, handlingResult);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除收文办理单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:delete')")
    public CommonResult<Boolean> deleteIncomingDocumentBill(@RequestParam("id") Long id) {
        incomingDocumentBillService.deleteIncomingDocumentBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除收文办理单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:delete')")
    public CommonResult<Boolean> deleteIncomingDocumentBillList(@RequestParam("ids") List<Long> ids) {
        incomingDocumentBillService.deleteIncomingDocumentBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得收文办理单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasAnyPermissions('oa:incoming-document-bill:create', 'oa:incoming-document-bill:query', 'oa:incoming-document-bill:submit')")
    public CommonResult<IncomingDocumentBillRespVO> getIncomingDocumentBill(@RequestParam("id") Long id) {
        IncomingDocumentBillRespVO respVO = incomingDocumentBillService.getIncomingDocumentBillInfo(id);
        billCorrectionDisplayEnricher.enrichIncomingDocumentBills(List.of(respVO));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得收文办理单分页")
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:query')")
    public CommonResult<PageResult<IncomingDocumentBillRespVO>> getIncomingDocumentBillPage(@Valid IncomingDocumentBillPageReqVO pageReqVO) {
        PageResult<IncomingDocumentBillDO> pageResult = incomingDocumentBillService.getIncomingDocumentBillPage(pageReqVO);
        PageResult<IncomingDocumentBillRespVO> voPage = BeanUtils.toBean(pageResult, IncomingDocumentBillRespVO.class);
        billCorrectionDisplayEnricher.enrichIncomingDocumentBills(voPage.getList());
        return success(voPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出收文办理单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:incoming-document-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportIncomingDocumentBillExcel(@Valid IncomingDocumentBillPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<IncomingDocumentBillDO> list = incomingDocumentBillService.getIncomingDocumentBillPage(pageReqVO).getList();
        ExcelUtils.write(response, "收文办理单.xls", "数据", IncomingDocumentBillRespVO.class,
                        BeanUtils.toBean(list, IncomingDocumentBillRespVO.class));
    }

}
