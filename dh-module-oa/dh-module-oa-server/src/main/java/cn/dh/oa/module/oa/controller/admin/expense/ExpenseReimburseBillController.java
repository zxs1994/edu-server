package cn.dh.oa.module.oa.controller.admin.expense;

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

import cn.dh.oa.module.oa.controller.admin.expense.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.service.expense.ExpenseReimburseBillService;

@Tag(name = "管理后台 - 费用报销单")
@RestController
@RequestMapping("/oa/expense-reimburse-bill")
@Validated
public class ExpenseReimburseBillController {

    @Resource
    private ExpenseReimburseBillService expenseReimburseBillService;

    @PostMapping("/create")
    @Operation(summary = "创建费用报销单")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:create')")
    public CommonResult<Long> createExpenseReimburseBill(@Valid @RequestBody ExpenseReimburseBillSaveReqVO createReqVO) {
        return success(expenseReimburseBillService.createExpenseReimburseBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存费用报销单")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:create')")
    public CommonResult<Long> saveExpenseReimburseBill(@Valid @RequestBody ExpenseReimburseBillSaveReqVO saveReqVO) {
        return success(expenseReimburseBillService.saveExpenseReimburseBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交费用报销单")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:create')")
    public CommonResult<Long> submitExpenseReimburseBill(@Valid @RequestBody ExpenseReimburseBillSaveReqVO submitReqVO) {
        return success(expenseReimburseBillService.submitExpenseReimburseBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新费用报销单")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:update')")
    public CommonResult<Boolean> updateExpenseReimburseBill(@Valid @RequestBody ExpenseReimburseBillSaveReqVO updateReqVO) {
        expenseReimburseBillService.updateExpenseReimburseBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除费用报销单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:delete')")
    public CommonResult<Boolean> deleteExpenseReimburseBill(@RequestParam("id") Long id) {
        expenseReimburseBillService.deleteExpenseReimburseBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除费用报销单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:delete')")
    public CommonResult<Boolean> deleteExpenseReimburseBillList(@RequestParam("ids") List<Long> ids) {
        expenseReimburseBillService.deleteExpenseReimburseBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得费用报销单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:query')")
    public CommonResult<ExpenseReimburseBillRespVO> getExpenseReimburseBill(@RequestParam("id") Long id) {
        ExpenseReimburseBillRespVO respVO = expenseReimburseBillService.getExpenseReimburseBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得费用报销单分页")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:query')")
    public CommonResult<PageResult<ExpenseReimburseBillRespVO>> getExpenseReimburseBillPage(@Valid ExpenseReimburseBillPageReqVO pageReqVO) {
        PageResult<ExpenseReimburseBillDO> pageResult = expenseReimburseBillService.getExpenseReimburseBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ExpenseReimburseBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出费用报销单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:expense-reimburse-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExpenseReimburseBillExcel(@Valid ExpenseReimburseBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExpenseReimburseBillDO> list = expenseReimburseBillService.getExpenseReimburseBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "费用报销单.xls", "数据", ExpenseReimburseBillRespVO.class,
                        BeanUtils.toBean(list, ExpenseReimburseBillRespVO.class));
    }

}
