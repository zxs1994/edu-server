package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
import cn.dh.oa.module.oa.util.OaMoneyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 费用支出申请模板导出数据组装
 */
@Component
public class ExpensePaymentExportMapBuilder {

    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批";
    private static final String PROOF_LABEL = "证明或验收";
    private static final String HANDLER_LABEL = "经手";
    private static final String FIXED_AUDITOR = "胡建国";
    private static final String FIXED_APPROVER = "沈建华";
    private static final String FIXED_PROOF = "于芯菲";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d");

    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportData> buildExportData(ExpensePaymentBillRespVO bill) {
        BillExportData page = new BillExportData();
        page.setMainFields(buildMainFields(bill));
        page.setSignatureImages(buildSignatureImages(bill));
        return List.of(page);
    }

    private Map<String, Object> buildMainFields(ExpensePaymentBillRespVO bill) {
        Map<String, Object> main = new HashMap<>();
        putApplyDate(main, bill);
        main.put("billCode", valueOrEmpty(bill.getBillCode()));
        main.put("payeeName", firstNotBlank(bill.getPayeeCompanyName(), bill.getPayeePersonName()));
        main.put("payeeAccount", valueOrEmpty(bill.getPayeeAccount()));
        main.put("payeeBank", valueOrEmpty(bill.getPayeeBank()));
        main.put("cause", valueOrEmpty(bill.getCause()));
        main.put("amountChinese", OaMoneyUtils.toChineseUpper(bill.getTotalAmount()));
        main.put("totalAmount", OaMoneyUtils.toMoneyValue(bill.getTotalAmount()));
        main.put("totalAmountText", OaMoneyUtils.formatWithComma(bill.getTotalAmount()));
        main.put("attachmentCount", bill.getAttachments() == null
                ? "" : String.valueOf(bill.getAttachments().size()));
        return main;
    }

    private void putApplyDate(Map<String, Object> main, ExpensePaymentBillRespVO bill) {
        LocalDate applyDate = bill.getApplyDate();
        if (applyDate == null) {
            LocalDateTime createTime = bill.getCreateTime();
            applyDate = createTime == null ? null : createTime.toLocalDate();
        }
        if (applyDate == null) {
            main.put("year", "");
            main.put("month", "");
            main.put("day", "");
            return;
        }
        main.put("year", YEAR_FORMATTER.format(applyDate));
        main.put("month", MONTH_FORMATTER.format(applyDate));
        main.put("day", DAY_FORMATTER.format(applyDate));
    }

    private List<BillExportImage> buildSignatureImages(ExpensePaymentBillRespVO bill) {
        List<BillExportImage> images = new ArrayList<>();
        addSignatureByNickname(images, APPROVE_LABEL, FIXED_APPROVER);
        addSignatureByNickname(images, AUDIT_LABEL, FIXED_AUDITOR);
        addSignatureByNickname(images, PROOF_LABEL, FIXED_PROOF);
        addSignatureByNickname(images, HANDLER_LABEL, bill.getCreatorName());
        return images;
    }

    private void addSignatureByNickname(List<BillExportImage> images, String label, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return;
        }
        signatureTemplateLoader.loadByNickname(nickname.trim()).ifPresent(loaded -> {
            BillExportImage image = new BillExportImage();
            image.setAnchorLabel(label);
            image.setData(loaded.data());
            image.setPictureType(loaded.pictureType());
            image.setInsertInLabelRegion(true);
            images.add(image);
        });
    }

    private String firstNotBlank(String first, String second) {
        return first == null || first.isBlank() ? valueOrEmpty(second) : first;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
