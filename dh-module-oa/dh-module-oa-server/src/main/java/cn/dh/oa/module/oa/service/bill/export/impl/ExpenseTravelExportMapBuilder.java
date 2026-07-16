package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.dict.core.DictFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseDetailRespVO;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
import cn.dh.oa.module.oa.util.OaMoneyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 差旅报销单模板导出数据组装
 */
@Component
public class ExpenseTravelExportMapBuilder {

    private static final String TRAVEL_EXPENSE_TYPE_DICT = "oa_travel_expense_type";
    private static final String TRANSPORT_TYPE_DICT = "oa_transport_type";
    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批";
    private static final String PROOF_LABEL = "证明或验收";
    private static final String HANDLER_LABEL = "经手";
    private static final String FIXED_AUDITOR = "胡建国";
    private static final String FIXED_APPROVER = "沈建华";
    private static final String FIXED_PROOF = "于芯菲";
    private static final int DETAIL_MAX_ROWS = 8;
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private static final BigDecimal CITY_STANDARD = new BigDecimal("80");
    private static final BigDecimal MEAL_STANDARD = new BigDecimal("100");

    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportData> buildPagedExportData(ExpenseReimburseBillRespVO bill) {
        List<ExpenseReimburseDetailRespVO> details = bill.getDetails() == null
                ? Collections.emptyList() : bill.getDetails();
        int totalPages = Math.max(1, (details.size() + DETAIL_MAX_ROWS - 1) / DETAIL_MAX_ROWS);

        BigDecimal people = resolveTravelerCount(bill);
        BigDecimal travelDays = sumTravelDays(bill.getTravelBills());
        BigDecimal citySubsidyAmount = citySubsidyAmount(people, travelDays);
        BigDecimal mealSubsidyAmount = mealSubsidyAmount(people, travelDays);
        List<BillExportImage> signatureImages = buildSignatureImages(bill);

        List<BillExportData> pages = new ArrayList<>(totalPages);
        for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
            int from = pageIndex * DETAIL_MAX_ROWS;
            int to = Math.min(from + DETAIL_MAX_ROWS, details.size());
            List<ExpenseReimburseDetailRespVO> pageDetails = details.subList(from, to);
            boolean includeSubsidy = pageIndex == 0;
            BigDecimal pageHotelAmount = sumPageHotelAmount(pageDetails);
            BigDecimal pageTotalAmount = pageHotelAmount;
            if (includeSubsidy) {
                pageTotalAmount = pageTotalAmount
                        .add(citySubsidyAmount)
                        .add(mealSubsidyAmount);
            }
            pageTotalAmount = pageTotalAmount.setScale(2, RoundingMode.HALF_UP);

            BillExportData page = new BillExportData();
            page.setMainFields(buildMainFields(
                    bill, pageTotalAmount, people, travelDays,
                    includeSubsidy ? citySubsidyAmount : null,
                    includeSubsidy ? mealSubsidyAmount : null,
                    includeSubsidy
            ));
            page.setDetailList(buildDetailRows(pageDetails));
            page.setSignatureImages(signatureImages);
            pages.add(page);
        }
        return pages;
    }

    private Map<String, Object> buildMainFields(
            ExpenseReimburseBillRespVO bill,
            BigDecimal pageAmount,
            BigDecimal people,
            BigDecimal travelDays,
            BigDecimal citySubsidyAmount,
            BigDecimal mealSubsidyAmount,
            boolean includeSubsidy
    ) {
        Map<String, Object> main = new HashMap<>();
        LocalDateTime createTime = bill.getCreateTime();
        if (createTime != null) {
            main.put("year", YEAR_FORMATTER.format(createTime));
            main.put("month", MONTH_FORMATTER.format(createTime));
            main.put("day", DAY_FORMATTER.format(createTime));
            main.put("createDateText", YEAR_FORMATTER.format(createTime) + "年"
                    + MONTH_FORMATTER.format(createTime) + "月"
                    + DAY_FORMATTER.format(createTime) + "日");
        } else {
            main.put("year", "");
            main.put("month", "");
            main.put("day", "");
            main.put("createDateText", "");
        }
        main.put("creatorName", valueOrEmpty(bill.getCreatorName()));
        String cause = valueOrFallback(bill.getTravelCause(), bill.getCause());
        main.put("cause", cause);
        main.put("causeText", cause);
        main.put("pageTotalAmountCn", OaMoneyUtils.toChineseUpper(pageAmount));
        main.put("pageTotalAmount", OaMoneyUtils.toMoneyValue(pageAmount));
        main.put("totalAmountCn", OaMoneyUtils.toChineseUpper(pageAmount));
        main.put("totalAmount", OaMoneyUtils.toMoneyValue(pageAmount));
        main.put("attachmentCount", bill.getAttachments() == null ? "0" : String.valueOf(bill.getAttachments().size()));
        // 附件总数量：费用明细「单据张数」之和
        main.put("receiptCount", sumReceiptCount(bill.getDetails()));
        // 人数（含本人）
        main.put("travelerCount", stripTrailingZeros(people));

        if (includeSubsidy) {
            main.put("subsidyPeople", stripTrailingZeros(people));
            main.put("subsidyDays", stripTrailingZeros(travelDays));
            main.put("subsidyCityAmount", OaMoneyUtils.toMoneyValue(citySubsidyAmount));
            main.put("subsidyMealAmount", OaMoneyUtils.toMoneyValue(mealSubsidyAmount));
        } else {
            main.put("subsidyPeople", "");
            main.put("subsidyDays", "");
            main.put("subsidyCityAmount", null);
            main.put("subsidyMealAmount", null);
        }
        return main;
    }

    /** 表单填写的补贴领取人数，缺省按 1 */
    private BigDecimal resolveTravelerCount(ExpenseReimburseBillRespVO bill) {
        Integer count = bill.getTravelerCount();
        if (count == null || count < 1) {
            return BigDecimal.ONE;
        }
        return BigDecimal.valueOf(count);
    }

    private List<Map<String, Object>> buildDetailRows(List<ExpenseReimburseDetailRespVO> details) {
        List<Map<String, Object>> rows = new ArrayList<>(details.size());
        for (ExpenseReimburseDetailRespVO detail : details) {
            Map<String, Object> row = new HashMap<>();
            LocalDate expenseDate = detail.getExpenseDate();
            if (expenseDate != null) {
                String month = String.valueOf(expenseDate.getMonthValue());
                String day = String.valueOf(expenseDate.getDayOfMonth());
                row.put("startMonth", month);
                row.put("startDay", day);
                row.put("endMonth", month);
                row.put("endDay", day);
                row.put("depMonth", month);
                row.put("depDay", day);
            }
            row.put("departure", valueOrEmpty(detail.getDeparture()));
            row.put("destination", valueOrEmpty(detail.getDestination()));
            String expenseTypeLabel = resolveTravelExpenseTypeLabel(detail.getExpenseType());
            // 交通工具：取明细字段，字典转中文
            row.put("transportType", resolveTransportTypeLabel(detail.getTransportType()));
            row.put("expenseType", expenseTypeLabel);
            row.put("description", valueOrEmpty(detail.getDescription()));
            // 单据数量：取明细「单据张数」
            row.put("receiptCount", detail.getReceiptCount() == null
                    ? "" : Integer.valueOf(detail.getReceiptCount()));
            row.put("trafficAmount", OaMoneyUtils.toMoneyValue(detail.getAmount()));
            row.put("hotelItem", expenseTypeLabel);
            row.put("hotelAmount", OaMoneyUtils.toMoneyValue(detail.getAmount()));
            row.put("amount", OaMoneyUtils.toMoneyValue(detail.getAmount()));
            rows.add(row);
        }
        return rows;
    }

    /** 本页住宿费合计（对应模板 {.hotelAmount} 列） */
    private BigDecimal sumPageHotelAmount(List<ExpenseReimburseDetailRespVO> details) {
        BigDecimal total = BigDecimal.ZERO;
        for (ExpenseReimburseDetailRespVO detail : details) {
            if (detail.getAmount() != null) {
                total = total.add(detail.getAmount());
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /** 费用明细单据张数合计（主表附件总数量） */
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

    private BigDecimal sumTravelDays(List<TravelApplyBillRespVO> travelBills) {
        if (travelBills == null || travelBills.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (TravelApplyBillRespVO travelBill : travelBills) {
            if (travelBill.getTravelDays() != null) {
                total = total.add(travelBill.getTravelDays());
            }
        }
        return total.setScale(0, RoundingMode.CEILING);
    }

    private BigDecimal citySubsidyAmount(BigDecimal people, BigDecimal days) {
        return CITY_STANDARD.multiply(people).multiply(days).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal mealSubsidyAmount(BigDecimal people, BigDecimal days) {
        return MEAL_STANDARD.multiply(people).multiply(days).setScale(2, RoundingMode.HALF_UP);
    }

    private List<BillExportImage> buildSignatureImages(ExpenseReimburseBillRespVO bill) {
        List<BillExportImage> images = new ArrayList<>();
        // 经手：发起人；证明或验收/审核/审批：固定人员
        addSignatureByNickname(images, HANDLER_LABEL, bill.getCreatorName());
        addSignatureByNickname(images, PROOF_LABEL, FIXED_PROOF);
        addSignatureByNickname(images, AUDIT_LABEL, FIXED_AUDITOR);
        addSignatureByNickname(images, APPROVE_LABEL, FIXED_APPROVER);
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
            // J19:K19 签名区较宽，铺满区域并允许放大
            image.setMaxSignWidthPx(0);
            image.setAllowUpscale(true);
            images.add(image);
        });
    }

    private String stripTrailingZeros(BigDecimal number) {
        if (number == null) {
            return "";
        }
        return number.stripTrailingZeros().toPlainString();
    }

    private String valueOrFallback(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second == null ? "" : second;
    }

    private String resolveTravelExpenseTypeLabel(String expenseType) {
        if (expenseType == null || expenseType.isBlank()) {
            return "";
        }
        String label = DictFrameworkUtils.parseDictDataLabel(TRAVEL_EXPENSE_TYPE_DICT, expenseType);
        return label != null ? label : expenseType;
    }

    private String resolveTransportTypeLabel(Integer transportType) {
        if (transportType == null) {
            return "";
        }
        String label = DictFrameworkUtils.parseDictDataLabel(TRANSPORT_TYPE_DICT, transportType);
        return label != null ? label : String.valueOf(transportType);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

}
