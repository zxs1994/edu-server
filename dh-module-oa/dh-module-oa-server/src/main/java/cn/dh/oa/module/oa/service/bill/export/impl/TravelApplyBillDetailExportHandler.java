package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.export.BillDetailExportHandler;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillTemplateExporter;
import cn.dh.oa.module.oa.service.travel.TravelApplyBillService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 出差申请单详情导出
 */
@Component
public class TravelApplyBillDetailExportHandler implements BillDetailExportHandler {

    private static final String TEMPLATE_CLASSPATH = "excel-templates/出差申请单-支持打印A4.xlsx";

    @Resource
    private TravelApplyBillService travelApplyBillService;
    @Resource
    private TravelApplyExportMapBuilder travelApplyExportMapBuilder;
    @Resource
    private BillTemplateExporter billTemplateExporter;

    @Override
    public OaBillTypeEnum supportBillType() {
        return OaBillTypeEnum.OA_TRAVEL_APPLY_BILL;
    }

    @Override
    public void export(Long id, HttpServletResponse response) throws IOException {
        TravelApplyBillRespVO bill = travelApplyBillService.getTravelApplyBillInfo(id);
        if (bill == null) {
            throw ServiceExceptionUtil.invalidParamException("单据不存在，无法导出");
        }
        List<BillExportData> pages = travelApplyExportMapBuilder.buildExportData(bill);
        String fileName = "出差申请单-" + (bill.getBillCode() == null ? id : bill.getBillCode()) + ".xlsx";
        billTemplateExporter.export(response, TEMPLATE_CLASSPATH, fileName, pages);
    }

}
