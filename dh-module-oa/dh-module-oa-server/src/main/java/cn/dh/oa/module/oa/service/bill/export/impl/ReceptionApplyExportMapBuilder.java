package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.dict.core.DictFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
import cn.dh.oa.module.oa.util.OaMoneyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接待申请单模板导出数据组装
 */
@Component
public class ReceptionApplyExportMapBuilder {

    private static final String DINING_STANDARD_DICT = "oa_reception_dining_standard";
    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批";
    private static final String FIXED_AUDITOR = "胡建国";
    private static final String FIXED_APPROVER = "沈建华";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter DATE_TEXT_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");
    private static final DateTimeFormatter DINING_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportData> buildExportData(ReceptionApplyBillRespVO bill) {
        BillExportData page = new BillExportData();
        page.setMainFields(buildMainFields(bill));
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

    private Map<String, Object> buildMainFields(ReceptionApplyBillRespVO bill) {
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
        main.put("deptName", valueOrEmpty(bill.getDeptName()));
        main.put("diningTime", formatDiningTime(bill.getDiningTime()));
        main.put("diningStandard", resolveDiningStandardLabel(bill.getDiningStandard()));
        main.put("guestCount", formatCount(bill.getGuestCount()));
        main.put("accompanyCount", formatCount(bill.getAccompanyCount()));
        main.put("estimatedCost", OaMoneyUtils.formatWithComma(bill.getEstimatedCost()));
        return main;
    }

    private String formatDiningTime(LocalDateTime diningTime) {
        return diningTime == null ? "" : DINING_TIME_FORMATTER.format(diningTime);
    }

    private String resolveDiningStandardLabel(String diningStandard) {
        if (diningStandard == null || diningStandard.isBlank()) {
            return "";
        }
        String label = DictFrameworkUtils.parseDictDataLabel(DINING_STANDARD_DICT, diningStandard);
        return label != null ? label : diningStandard;
    }

    private String formatCount(Integer count) {
        return count == null ? "" : String.valueOf(count);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

}
