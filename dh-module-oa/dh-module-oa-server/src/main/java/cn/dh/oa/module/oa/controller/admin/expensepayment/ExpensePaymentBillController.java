package cn.dh.oa.module.oa.controller.admin.expensepayment;

import cn.dh.oa.framework.apilog.core.annotation.ApiAccessLog;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.common.pojo.PageParam;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.framework.excel.core.util.ExcelUtils;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillPageReqVO;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillSaveReqVO;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentBillDO;
import cn.dh.oa.module.oa.service.correction.BillCorrectionDisplayEnricher;
import cn.dh.oa.module.oa.service.expensepayment.ExpensePaymentBillService;
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

@Tag(name = "管理后台 - 费用支出申请")
@RestController
@RequestMapping("/oa/expense-payment-bill")
@Validated
public class ExpensePaymentBillController {

    @Resource
    private ExpensePaymentBillService expensePaymentBillService;
    @Resource
    private BillCorrectionDisplayEnricher billCorrectionDisplayEnricher;

    @PostMapping("/save")
    @Operation(summary = "保存费用支出申请")
    @PreAuthorize("@ss.hasAnyPermissions('oa:expense-payment-bill:create', 'oa:expense-payment-bill:query', 'oa:expense-payment-bill:submit')")
    public CommonResult<Long> saveExpensePaymentBill(@Valid @RequestBody ExpensePaymentBillSaveReqVO saveReqVO) {
        return success(expensePaymentBillService.saveExpensePaymentBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交费用支出申请")
    @PreAuthorize("@ss.hasAnyPermissions('oa:expense-payment-bill:create', 'oa:expense-payment-bill:query', 'oa:expense-payment-bill:submit')")
    public CommonResult<Long> submitExpensePaymentBill(@Valid @RequestBody ExpensePaymentBillSaveReqVO submitReqVO) {
        return success(expensePaymentBillService.submitExpensePaymentBill(submitReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除费用支出申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:expense-payment-bill:delete')")
    public CommonResult<Boolean> deleteExpensePaymentBill(@RequestParam("id") Long id) {
        expensePaymentBillService.deleteExpensePaymentBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除费用支出申请")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:expense-payment-bill:delete')")
    public CommonResult<Boolean> deleteExpensePaymentBillList(@RequestParam("ids") @NotEmpty List<Long> ids) {
        expensePaymentBillService.deleteExpensePaymentBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得费用支出申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('oa:expense-payment-bill:create', 'oa:expense-payment-bill:query', 'oa:expense-payment-bill:submit')")
    public CommonResult<ExpensePaymentBillRespVO> getExpensePaymentBill(@RequestParam("id") Long id) {
        ExpensePaymentBillRespVO respVO = expensePaymentBillService.getExpensePaymentBillInfo(id);
        billCorrectionDisplayEnricher.enrichExpensePaymentBills(List.of(respVO));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得费用支出申请分页")
    @PreAuthorize("@ss.hasPermission('oa:expense-payment-bill:query')")
    public CommonResult<PageResult<ExpensePaymentBillRespVO>> getExpensePaymentBillPage(
            @Valid ExpensePaymentBillPageReqVO pageReqVO) {
        PageResult<ExpensePaymentBillDO> pageResult = expensePaymentBillService.getExpensePaymentBillPage(pageReqVO);
        PageResult<ExpensePaymentBillRespVO> voPage = BeanUtils.toBean(pageResult, ExpensePaymentBillRespVO.class);
        billCorrectionDisplayEnricher.enrichExpensePaymentBills(voPage.getList());
        return success(voPage);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出费用支出申请 Excel")
    @PreAuthorize("@ss.hasPermission('oa:expense-payment-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExpensePaymentBillExcel(@Valid ExpensePaymentBillPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExpensePaymentBillDO> list = expensePaymentBillService.getExpensePaymentBillPage(pageReqVO).getList();
        ExcelUtils.write(response, "费用支出申请.xls", "数据", ExpensePaymentBillRespVO.class,
                BeanUtils.toBean(list, ExpensePaymentBillRespVO.class));
    }

}
