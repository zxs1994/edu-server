package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.dict.core.DictFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseDetailRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
import cn.dh.oa.module.oa.util.OaMoneyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 日常报销单模板导出数据组装
 */
@Component
public class DailyExpenseExportMapBuilder {

    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批人";
    private static final String PROOF_LABEL = "证明或验收";
    private static final String HANDLER_LABEL = "经手";
    private static final String FIXED_AUDITOR = "胡建国";
    private static final String FIXED_APPROVER = "沈建华";
    private static final String FIXED_PROOF = "于芯菲";
    private static final String EXPENSE_TYPE_DICT = "oa_expense_type";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter DATE_TEXT_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");

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
        List<String> expenseTypeLabels = resolveExpenseTypeLabels(bill.getDetails());
        main.put("companyName", joinWithSlash(bill.getCompanyName(), bill.getDeptName()));
        main.put("deptName", valueOrEmpty(bill.getDeptName()));
        main.put("creatorName", valueOrEmpty(bill.getCreatorName()));
        main.put("cause", buildExpenseTypeSummary(expenseTypeLabels));
        main.put("remark", valueOrEmpty(bill.getRemark()));
        // 标题括号内：多个费用类型用「/」拼接
        main.put("expenseType", String.join("/", expenseTypeLabels));
        // 数字类型，供 Excel 货币/数值格式单元格使用
        main.put("totalAmount", OaMoneyUtils.toMoneyValue(bill.getTotalAmount()));
        main.put("totalAmountText", OaMoneyUtils.formatWithComma(bill.getTotalAmount()));
        main.put("amountChinese", OaMoneyUtils.toChineseUpper(bill.getTotalAmount()));
        main.put("attachmentCount", bill.getAttachments() == null
                ? "" : String.valueOf(bill.getAttachments().size()));
        // 附单据张数：费用明细「单据张数」之和
        main.put("receiptCount", sumReceiptCount(bill.getDetails()));
        return main;
    }

    private String sumReceiptCount(List<ExpenseReimburseDetailRespVO> details) {
        if (details == null || details.isEmpty()) {
            return "";
        }
        int total = details.stream()
                .map(ExpenseReimburseDetailRespVO::getReceiptCount)
                .filter(count -> count != null)
                .mapToInt(Integer::intValue)
                .sum();
        return String.valueOf(total);
    }

    private List<String> resolveExpenseTypeLabels(List<ExpenseReimburseDetailRespVO> details) {
        if (details == null || details.isEmpty()) {
            return List.of();
        }
        Set<String> labels = new LinkedHashSet<>();
        details.stream()
                .sorted(Comparator
                        .comparing(ExpenseReimburseDetailRespVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(ExpenseReimburseDetailRespVO::getId, Comparator.nullsLast(Long::compareTo)))
                .forEach(detail -> {
                    String label = resolveExpenseTypeLabel(detail.getExpenseType());
                    if (!label.isBlank()) {
                        labels.add(label);
                    }
                });
        return new ArrayList<>(labels);
    }

    private String resolveExpenseTypeLabel(String expenseType) {
        if (expenseType == null || expenseType.isBlank()) {
            return "";
        }
        String label = DictFrameworkUtils.parseDictDataLabel(EXPENSE_TYPE_DICT, expenseType);
        return label != null ? label : expenseType;
    }

    private String buildExpenseTypeSummary(List<String> expenseTypeLabels) {
        if (expenseTypeLabels == null || expenseTypeLabels.isEmpty()) {
            return "";
        }
        return String.join("，", expenseTypeLabels) + "。";
    }

    private String joinWithSlash(String first, String second) {
        String left = valueOrEmpty(first);
        String right = valueOrEmpty(second);
        if (left.isEmpty()) {
            return right;
        }
        if (right.isEmpty()) {
            return left;
        }
        return left + "/" + right;
    }

    private List<BillExportImage> buildSignatureImages(ExpenseReimburseBillRespVO bill) {
        List<BillExportImage> images = new ArrayList<>();
        // 领款人签章留空；经手用发起人；审批人/审核/证明固定人员
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
            images.add(image);
        });
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

}
