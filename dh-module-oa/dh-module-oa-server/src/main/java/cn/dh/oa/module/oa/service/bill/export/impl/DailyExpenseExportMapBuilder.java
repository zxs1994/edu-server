package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.dict.core.DictFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseDetailRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaBillExportSignatureResolver;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
import cn.dh.oa.module.oa.util.OaMoneyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日常报销单模板导出数据组装
 */
@Component
public class DailyExpenseExportMapBuilder {

    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批人";
    private static final String PAYEE_LABEL = "领款人签章";
    private static final String EXPENSE_TYPE_DICT = "oa_expense_type";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter DATE_TEXT_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");

    @Resource
    private OaBillExportSignatureResolver oaBillExportSignatureResolver;
    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportData> buildExportData(ExpenseReimburseBillRespVO bill) {
        BillExportData page = new BillExportData();
        page.setMainFields(buildMainFields(bill));
        page.setSignatureImages(buildSignatureImages(bill));
        return List.of(page);
    }

    private Map<String, Object> buildMainFields(ExpenseReimburseBillRespVO bill) {
        Map<String, Object> main = new HashMap<>();
        LocalDateTime createTime = bill.getCreateTime();
        if (createTime != null) {
            main.put("year", YEAR_FORMATTER.format(createTime));
            main.put("month", MONTH_FORMATTER.format(createTime));
            main.put("day", DAY_FORMATTER.format(createTime));
            main.put("createDateText", DATE_TEXT_FORMATTER.format(createTime));
        } else {
            main.put("year", "");
            main.put("month", "");
            main.put("day", "");
            main.put("createDateText", "");
        }
        main.put("billCode", valueOrEmpty(bill.getBillCode()));
        main.put("companyName", valueOrEmpty(bill.getCompanyName()));
        main.put("deptName", valueOrEmpty(bill.getDeptName()));
        main.put("creatorName", valueOrEmpty(bill.getCreatorName()));
        main.put("cause", valueOrEmpty(bill.getCause()));
        main.put("remark", valueOrEmpty(bill.getRemark()));
        // 标题括号内：首条明细费用类型字典 label
        main.put("expenseType", resolveFirstExpenseTypeLabel(bill.getDetails()));
        // 数字类型，供 Excel 货币/数值格式单元格使用
        main.put("totalAmount", OaMoneyUtils.toMoneyValue(bill.getTotalAmount()));
        main.put("totalAmountText", OaMoneyUtils.formatWithComma(bill.getTotalAmount()));
        main.put("amountChinese", OaMoneyUtils.toChineseUpper(bill.getTotalAmount()));
        main.put("attachmentCount", bill.getAttachments() == null
                ? "" : String.valueOf(bill.getAttachments().size()));
        return main;
    }

    private String resolveFirstExpenseTypeLabel(List<ExpenseReimburseDetailRespVO> details) {
        if (details == null || details.isEmpty()) {
            return "";
        }
        ExpenseReimburseDetailRespVO first = details.stream()
                .sorted(Comparator
                        .comparing(ExpenseReimburseDetailRespVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(ExpenseReimburseDetailRespVO::getId, Comparator.nullsLast(Long::compareTo)))
                .findFirst()
                .orElse(null);
        if (first == null || first.getExpenseType() == null || first.getExpenseType().isBlank()) {
            return "";
        }
        String label = DictFrameworkUtils.parseDictDataLabel(EXPENSE_TYPE_DICT, first.getExpenseType());
        return label != null ? label : "";
    }

    private List<BillExportImage> buildSignatureImages(ExpenseReimburseBillRespVO bill) {
        List<BillExportImage> images = new ArrayList<>();
        // 领款人签章：申请人签名 → 插到「领款人签章」右侧空格
        addSignatureByNickname(images, PAYEE_LABEL, bill.getCreatorName());
        // 经手、证明或验收留空；审核/审批人按节点关键字
        images.addAll(oaBillExportSignatureResolver.resolveByKeywords(
                bill.getProcessInstanceId(), "审核", "审批", AUDIT_LABEL, APPROVE_LABEL));
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
            images.add(image);
        });
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

}
