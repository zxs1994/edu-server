package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillRespVO;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.export.BillDetailExportHandler;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillTemplateExporter;
import cn.dh.oa.module.oa.service.reception.ReceptionApplyBillService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 接待申请单详情导出
 */
@Component
public class ReceptionApplyBillDetailExportHandler implements BillDetailExportHandler {

    private static final String TEMPLATE_CLASSPATH = "excel-templates/接待申请单.xlsx";

    @Resource
    private ReceptionApplyBillService receptionApplyBillService;
    @Resource
    private ReceptionApplyExportMapBuilder receptionApplyExportMapBuilder;
    @Resource
    private BillTemplateExporter billTemplateExporter;

    @Override
    public OaBillTypeEnum supportBillType() {
        return OaBillTypeEnum.OA_RECEPTION_APPLY_BILL;
    }

    @Override
    public void export(Long id, HttpServletResponse response) throws IOException {
        ReceptionApplyBillRespVO bill = receptionApplyBillService.getReceptionApplyBillInfo(id);
        if (bill == null) {
            throw ServiceExceptionUtil.invalidParamException("单据不存在，无法导出");
        }
        List<BillExportData> pages = receptionApplyExportMapBuilder.buildExportData(bill);
        String fileName = "接待申请单-" + (bill.getBillCode() == null ? id : bill.getBillCode()) + ".xlsx";
        billTemplateExporter.export(response, TEMPLATE_CLASSPATH, fileName, pages);
    }

}
