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
 * 日常报销单详情导出
 */
@Component
public class DailyExpenseBillDetailExportHandler implements BillDetailExportHandler {

    private static final String TEMPLATE_CLASSPATH = "excel-templates/日常报销单.xlsx";

    @Resource
    private ExpenseReimburseBillService expenseReimburseBillService;
    @Resource
    private DailyExpenseExportMapBuilder dailyExpenseExportMapBuilder;
    @Resource
    private BillTemplateExporter billTemplateExporter;

    @Override
    public OaBillTypeEnum supportBillType() {
        return OaBillTypeEnum.OA_DAILY_EXPENSE_BILL;
    }

    @Override
    public void export(Long id, HttpServletResponse response) throws IOException {
        ExpenseReimburseBillRespVO bill = expenseReimburseBillService.getExpenseReimburseBillInfo(id);
        if (bill == null) {
            throw ServiceExceptionUtil.invalidParamException("单据不存在，无法导出");
        }
        List<BillExportData> pages = dailyExpenseExportMapBuilder.buildExportData(bill);
        String fileName = "日常报销单-" + (bill.getBillCode() == null ? id : bill.getBillCode()) + ".xlsx";
        billTemplateExporter.export(response, TEMPLATE_CLASSPATH, fileName, pages);
    }

}
