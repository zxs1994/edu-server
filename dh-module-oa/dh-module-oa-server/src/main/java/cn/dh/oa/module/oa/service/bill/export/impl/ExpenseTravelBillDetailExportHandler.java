package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseBillRespVO;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.export.BillDetailExportHandler;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillTemplateExporter;
import cn.dh.oa.module.oa.service.expense.ExpenseReimburseBillService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 差旅报销单详情导出
 */
@Component
public class ExpenseTravelBillDetailExportHandler implements BillDetailExportHandler {

    private static final String TEMPLATE_CLASSPATH = "excel-templates/差旅报销单.xlsx";

    @Resource
    private ExpenseReimburseBillService expenseReimburseBillService;
    @Resource
    private ExpenseTravelExportMapBuilder expenseTravelExportMapBuilder;
    @Resource
    private BillTemplateExporter billTemplateExporter;

    @Override
    public OaBillTypeEnum supportBillType() {
        return OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL;
    }

    @Override
    public void export(Long id, HttpServletResponse response) throws IOException {
        ExpenseReimburseBillRespVO bill = expenseReimburseBillService.getExpenseReimburseBillInfo(id);
        if (bill == null) {
            throw ServiceExceptionUtil.invalidParamException("单据不存在，无法导出");
        }
        List<BillExportData> pages = expenseTravelExportMapBuilder.buildPagedExportData(bill);
        String billCode = bill.getBillCode() == null ? String.valueOf(id) : bill.getBillCode();
        String baseName = "差旅报销单-" + billCode;
        if (pages.size() <= 1) {
            billTemplateExporter.export(response, TEMPLATE_CLASSPATH, baseName + ".xlsx", pages);
            return;
        }
        billTemplateExporter.exportZip(response, TEMPLATE_CLASSPATH, baseName + ".zip", pages,
                pageIndex -> baseName + "-" + (pageIndex + 1) + ".xlsx");
    }

}
