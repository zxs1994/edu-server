package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseDetailRespVO;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.util.OaMoneyUtils;
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

    private static final int DETAIL_MAX_ROWS = 8;
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private static final BigDecimal CITY_STANDARD = new BigDecimal("80");
    private static final BigDecimal MEAL_STANDARD = new BigDecimal("100");

    public List<BillExportData> buildPagedExportData(ExpenseReimburseBillRespVO bill) {
        List<ExpenseReimburseDetailRespVO> details = bill.getDetails() == null
                ? Collections.emptyList() : bill.getDetails();
        int totalPages = Math.max(1, (details.size() + DETAIL_MAX_ROWS - 1) / DETAIL_MAX_ROWS);

        BigDecimal people = BigDecimal.ONE;
        BigDecimal travelDays = sumTravelDays(bill.getTravelBills());
        BigDecimal citySubsidyAmount = citySubsidyAmount(people, travelDays);
        BigDecimal mealSubsidyAmount = mealSubsidyAmount(people, travelDays);

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
        main.put("pageTotalAmount", OaMoneyUtils.formatWithComma(pageAmount));
        main.put("totalAmountCn", OaMoneyUtils.toChineseUpper(pageAmount));
        main.put("totalAmount", OaMoneyUtils.formatWithComma(pageAmount));
        main.put("attachmentCount", bill.getAttachments() == null ? "0" : String.valueOf(bill.getAttachments().size()));

        if (includeSubsidy) {
            main.put("subsidyPeople", stripTrailingZeros(people));
            main.put("subsidyDays", stripTrailingZeros(travelDays));
            main.put("subsidyCityAmount", OaMoneyUtils.formatWithComma(citySubsidyAmount));
            main.put("subsidyMealAmount", OaMoneyUtils.formatWithComma(mealSubsidyAmount));
        } else {
            main.put("subsidyPeople", "");
            main.put("subsidyDays", "");
            main.put("subsidyCityAmount", "");
            main.put("subsidyMealAmount", "");
        }
        return main;
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
            row.put("transportType", valueOrEmpty(detail.getExpenseType()));
            row.put("expenseType", valueOrEmpty(detail.getExpenseType()));
            row.put("description", valueOrEmpty(detail.getDescription()));
            row.put("trafficAmount", OaMoneyUtils.formatWithComma(detail.getAmount()));
            row.put("hotelItem", valueOrEmpty(detail.getExpenseType()));
            row.put("hotelAmount", OaMoneyUtils.formatWithComma(detail.getAmount()));
            row.put("amount", OaMoneyUtils.formatWithComma(detail.getAmount()));
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
        return total.setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal citySubsidyAmount(BigDecimal people, BigDecimal days) {
        return CITY_STANDARD.multiply(people).multiply(days).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal mealSubsidyAmount(BigDecimal people, BigDecimal days) {
        return MEAL_STANDARD.multiply(people).multiply(days).setScale(2, RoundingMode.HALF_UP);
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

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

}
