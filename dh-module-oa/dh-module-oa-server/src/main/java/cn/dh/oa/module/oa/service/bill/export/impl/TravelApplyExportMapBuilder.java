package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.dict.core.DictFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelItineraryRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出差申请单模板导出数据组装（国内/出境共用）
 */
@Component
public class TravelApplyExportMapBuilder {

    private static final int MIN_ITINERARY_ROWS = 3;
    private static final String TRANSPORT_TYPE_DICT = "oa_transport_type";
    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批";
    private static final String FIXED_AUDITOR = "胡建国";
    private static final String FIXED_APPROVER = "沈建华";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter DATE_TEXT_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");
    private static final DateTimeFormatter DATE_RANGE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportData> buildExportData(TravelApplyBillRespVO bill) {
        BillExportData page = new BillExportData();
        page.setMainFields(buildMainFields(bill));
        page.setDetailList(buildDetailRows(bill));
        page.setDetailForceNewRow(true);
        page.setSignatureImages(buildSignatureImages());
        return List.of(page);
    }

    private List<BillExportImage> buildSignatureImages() {
        List<BillExportImage> images = new ArrayList<>();
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
            images.add(image);
        });
    }

    private Map<String, Object> buildMainFields(TravelApplyBillRespVO bill) {
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
        main.put("cause", valueOrEmpty(bill.getCause()));
        main.put("creatorName", valueOrEmpty(bill.getCreatorName()));
        main.put("companion", valueOrEmpty(bill.getCompanion()));
        main.put("companionPart", buildCompanionPart(bill.getCompanion()));
        return main;
    }

    private List<Map<String, Object>> buildDetailRows(TravelApplyBillRespVO bill) {
        List<TravelItineraryRespVO> itineraries = bill.getItineraries() == null
                ? new ArrayList<>() : new ArrayList<>(bill.getItineraries());
        itineraries.sort(Comparator
                .comparing(TravelItineraryRespVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(TravelItineraryRespVO::getId, Comparator.nullsLast(Long::compareTo)));

        int displayRows = itineraries.size() <= MIN_ITINERARY_ROWS
                ? MIN_ITINERARY_ROWS
                : itineraries.size();
        List<Map<String, Object>> rows = new ArrayList<>(displayRows);
        for (int i = 0; i < displayRows; i++) {
            TravelItineraryRespVO itinerary = i < itineraries.size() ? itineraries.get(i) : null;
            rows.add(buildDetailRow(i + 1, itinerary));
        }
        return rows;
    }

    private static final String DATE_LABEL = "起讫日期（预计）";

    private Map<String, Object> buildDetailRow(int index, TravelItineraryRespVO itinerary) {
        Map<String, Object> row = new HashMap<>();
        row.put("locationLabel", "出差地点" + index);
        row.put("dateLabel", DATE_LABEL);
        if (itinerary == null) {
            row.put("route", "");
            row.put("dateRange", "");
            row.put("transportType", "");
            return row;
        }
        row.put("route", buildRoute(itinerary));
        row.put("dateRange", buildDateRange(itinerary.getStartDate(), itinerary.getEndDate()));
        row.put("transportType", resolveTransportLabel(itinerary.getTransportType()));
        return row;
    }

    private String buildCompanionPart(String companion) {
        String value = valueOrEmpty(companion);
        return value.isEmpty() ? "" : "、" + value;
    }

    private String buildRoute(TravelItineraryRespVO itinerary) {
        String departure = valueOrEmpty(itinerary.getDepartureCity());
        String destination = valueOrEmpty(itinerary.getDestinationCity());
        if (departure.isEmpty() && destination.isEmpty()) {
            return "";
        }
        if (departure.isEmpty()) {
            return destination;
        }
        if (destination.isEmpty()) {
            return departure;
        }
        return departure + "-" + destination;
    }

    private String buildDateRange(LocalDateTime start, LocalDateTime end) {
        String startText = formatDateRangeDate(start);
        String endText = formatDateRangeDate(end);
        if (startText.isEmpty() && endText.isEmpty()) {
            return "";
        }
        if (endText.isEmpty()) {
            return startText;
        }
        if (startText.isEmpty()) {
            return endText;
        }
        return startText + " - " + endText;
    }

    private String formatDateRangeDate(LocalDateTime dateTime) {
        return dateTime == null ? "" : DATE_RANGE_FORMATTER.format(dateTime);
    }

    private String resolveTransportLabel(Integer transportType) {
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
