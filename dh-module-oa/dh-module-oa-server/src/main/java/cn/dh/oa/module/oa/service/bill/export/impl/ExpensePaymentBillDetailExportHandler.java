package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillRespVO;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.export.BillDetailExportHandler;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillTemplateExporter;
import cn.dh.oa.module.oa.service.expensepayment.ExpensePaymentBillService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 费用支出申请详情导出
 */
@Component
public class ExpensePaymentBillDetailExportHandler implements BillDetailExportHandler {

    private static final String TEMPLATE_CLASSPATH = "excel-templates/费用支出申请-支持打印A4.xlsx";

    @Resource
    private ExpensePaymentBillService expensePaymentBillService;
    @Resource
    private ExpensePaymentExportMapBuilder expensePaymentExportMapBuilder;
    @Resource
    private BillTemplateExporter billTemplateExporter;

    @Override
    public OaBillTypeEnum supportBillType() {
        return OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL;
    }

    @Override
    public void export(Long id, HttpServletResponse response) throws IOException {
        ExpensePaymentBillRespVO bill = expensePaymentBillService.getExpensePaymentBillInfo(id);
        if (bill == null) {
            throw ServiceExceptionUtil.invalidParamException("单据不存在，无法导出");
        }
        List<BillExportData> pages = expensePaymentExportMapBuilder.buildExportData(bill);
        String fileName = "费用支出申请-" + (bill.getBillCode() == null ? id : bill.getBillCode()) + ".xlsx";
        billTemplateExporter.export(response, TEMPLATE_CLASSPATH, fileName, pages);
    }
}
