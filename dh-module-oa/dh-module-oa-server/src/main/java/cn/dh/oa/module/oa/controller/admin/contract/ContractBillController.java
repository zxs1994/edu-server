package cn.dh.oa.module.oa.controller.admin.contract;

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

import cn.dh.oa.module.oa.controller.admin.contract.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.module.oa.service.contract.ContractBillService;

@Tag(name = "管理后台 - 合同审批单")
@RestController
@RequestMapping("/oa/contract-bill")
@Validated
public class ContractBillController {

    @Resource
    private ContractBillService contractBillService;

    @PostMapping("/create")
    @Operation(summary = "创建合同审批单")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:create')")
    public CommonResult<Long> createContractBill(@Valid @RequestBody ContractBillSaveReqVO createReqVO) {
        return success(contractBillService.createContractBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存合同审批单")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:create')")
    public CommonResult<Long> saveContractBill(@Valid @RequestBody ContractBillSaveReqVO saveReqVO) {
        return success(contractBillService.saveContractBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交合同审批单")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:create')")
    public CommonResult<Long> submitContractBill(@Valid @RequestBody ContractBillSaveReqVO submitReqVO) {
        return success(contractBillService.submitContractBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新合同审批单")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:update')")
    public CommonResult<Boolean> updateContractBill(@Valid @RequestBody ContractBillSaveReqVO updateReqVO) {
        contractBillService.updateContractBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除合同审批单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:delete')")
    public CommonResult<Boolean> deleteContractBill(@RequestParam("id") Long id) {
        contractBillService.deleteContractBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除合同审批单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:delete')")
    public CommonResult<Boolean> deleteContractBillList(@RequestParam("ids") List<Long> ids) {
        contractBillService.deleteContractBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得合同审批单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:query')")
    public CommonResult<ContractBillRespVO> getContractBill(@RequestParam("id") Long id) {
        ContractBillRespVO respVO = contractBillService.getContractBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得合同审批单分页")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:query')")
    public CommonResult<PageResult<ContractBillRespVO>> getContractBillPage(@Valid ContractBillPageReqVO pageReqVO) {
        PageResult<ContractBillDO> pageResult = contractBillService.getContractBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ContractBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出合同审批单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:contract-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportContractBillExcel(@Valid ContractBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ContractBillDO> list = contractBillService.getContractBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "合同审批单.xls", "数据", ContractBillRespVO.class,
                        BeanUtils.toBean(list, ContractBillRespVO.class));
    }

}
