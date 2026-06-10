package cn.dh.oa.module.oa.controller.admin.document;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
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

import cn.dh.oa.module.oa.controller.admin.document.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.module.oa.service.document.DocumentDispatchBillService;

@Tag(name = "管理后台 - 公文发文单")
@RestController
@RequestMapping("/oa/document-dispatch-bill")
@Validated
public class DocumentDispatchBillController {

    @Resource
    private DocumentDispatchBillService documentDispatchBillService;

    @PostMapping("/create")
    @Operation(summary = "创建公文发文单")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:create')")
    public CommonResult<Long> createDocumentDispatchBill(@Valid @RequestBody DocumentDispatchBillSaveReqVO createReqVO) {
        return success(documentDispatchBillService.createDocumentDispatchBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存公文发文单")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:create')")
    public CommonResult<Long> saveDocumentDispatchBill(@Valid @RequestBody DocumentDispatchBillSaveReqVO saveReqVO) {
        return success(documentDispatchBillService.saveDocumentDispatchBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交公文发文单")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:create')")
    public CommonResult<Long> submitDocumentDispatchBill(@Valid @RequestBody DocumentDispatchBillSaveReqVO submitReqVO) {
        return success(documentDispatchBillService.submitDocumentDispatchBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新公文发文单")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:update')")
    public CommonResult<Boolean> updateDocumentDispatchBill(@Valid @RequestBody DocumentDispatchBillSaveReqVO updateReqVO) {
        documentDispatchBillService.updateDocumentDispatchBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公文发文单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:delete')")
    public CommonResult<Boolean> deleteDocumentDispatchBill(@RequestParam("id") Long id) {
        documentDispatchBillService.deleteDocumentDispatchBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除公文发文单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:delete')")
    public CommonResult<Boolean> deleteDocumentDispatchBillList(@RequestParam("ids") List<Long> ids) {
        documentDispatchBillService.deleteDocumentDispatchBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得公文发文单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:query')")
    public CommonResult<DocumentDispatchBillRespVO> getDocumentDispatchBill(@RequestParam("id") Long id) {
        DocumentDispatchBillRespVO respVO = documentDispatchBillService.getDocumentDispatchBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得公文发文单分页")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:query')")
    public CommonResult<PageResult<DocumentDispatchBillRespVO>> getDocumentDispatchBillPage(@Valid DocumentDispatchBillPageReqVO pageReqVO) {
        PageResult<DocumentDispatchBillDO> pageResult = documentDispatchBillService.getDocumentDispatchBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DocumentDispatchBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出公文发文单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:document-dispatch-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDocumentDispatchBillExcel(@Valid DocumentDispatchBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DocumentDispatchBillDO> list = documentDispatchBillService.getDocumentDispatchBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "公文发文单.xls", "数据", DocumentDispatchBillRespVO.class,
                        BeanUtils.toBean(list, DocumentDispatchBillRespVO.class));
    }

}
