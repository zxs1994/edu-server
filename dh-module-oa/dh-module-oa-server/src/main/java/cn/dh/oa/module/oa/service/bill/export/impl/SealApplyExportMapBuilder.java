package cn.dh.oa.module.oa.service.bill.export.impl;

import cn.dh.oa.framework.dict.core.DictFrameworkUtils;
import cn.dh.oa.module.oa.controller.admin.seal.vo.SealApplyBillRespVO;
import cn.dh.oa.module.oa.service.bill.export.BillExportData;
import cn.dh.oa.module.oa.service.bill.export.BillExportImage;
import cn.dh.oa.module.oa.service.bill.export.OaBillExportSignatureResolver;
import cn.dh.oa.module.oa.service.bill.export.OaSignatureTemplateLoader;
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
 * 用印申请单模板导出数据组装
 */
@Component
public class SealApplyExportMapBuilder {

    private static final String SEAL_TYPE_DICT = "oa_seal_type";
    private static final String KEEPER_LABEL = "印鉴管理人";
    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "批准";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("M");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter DATE_TEXT_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Resource
    private OaBillExportSignatureResolver oaBillExportSignatureResolver;
    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportData> buildExportData(SealApplyBillRespVO bill) {
        BillExportData page = new BillExportData();
        page.setMainFields(buildMainFields(bill));
        page.setSignatureImages(buildSignatureImages(bill));
        return List.of(page);
    }

    private Map<String, Object> buildMainFields(SealApplyBillRespVO bill) {
        Map<String, Object> main = new HashMap<>();
        LocalDateTime createTime = bill.getCreateTime();
        if (createTime != null) {
            main.put("year", YEAR_FORMATTER.format(createTime));
            main.put("month", MONTH_FORMATTER.format(createTime));
            main.put("day", DAY_FORMATTER.format(createTime));
            main.put("createDateText", DATE_TEXT_FORMATTER.format(createTime));
            main.put("applyDate", DATE_TEXT_FORMATTER.format(createTime));
        } else {
            main.put("year", "");
            main.put("month", "");
            main.put("day", "");
            main.put("createDateText", "");
            main.put("applyDate", "");
        }
        main.put("billCode", valueOrEmpty(bill.getBillCode()));
        main.put("creatorName", valueOrEmpty(bill.getCreatorName()));
        main.put("deptName", valueOrEmpty(bill.getDeptName()));
        main.put("cause", valueOrEmpty(bill.getCause()));
        main.put("documentTitle", valueOrEmpty(bill.getDocumentTitle()));
        main.put("documentType", valueOrEmpty(bill.getDocumentType()));
        // 印次：取文件份数
        main.put("documentCount", bill.getDocumentCount() == null
                ? "" : Integer.valueOf(bill.getDocumentCount()));
        main.put("destinationUnit", valueOrEmpty(bill.getDestinationUnit()));
        main.put("contractParty", valueOrEmpty(bill.getContractParty()));
        main.put("sealNo", valueOrEmpty(bill.getSealNo()));
        main.put("sealName", valueOrEmpty(bill.getSealName()));
        main.put("sealTypeName", resolveSealTypeLabel(bill.getSealType()));
        main.put("keeperName", valueOrEmpty(bill.getKeeperName()));
        main.put("expectedUseTime", formatDate(bill.getExpectedUseTime()));
        main.put("expectedReturnTime", formatDate(bill.getExpectedReturnTime()));
        main.put("remark", valueOrEmpty(bill.getRemark()));
        return main;
    }

    private List<BillExportImage> buildSignatureImages(SealApplyBillRespVO bill) {
        List<BillExportImage> images = new ArrayList<>();
        // 印鉴管理人：按保管人昵称找签名，找不到则跳过留空
        addSignatureByNickname(images, KEEPER_LABEL, bill.getKeeperName());
        // 审核 / 批准：走审批流；「审批」映射为「批准」；找不到图则 resolver 已跳过
        for (BillExportImage image : oaBillExportSignatureResolver.resolve(bill.getProcessInstanceId())) {
            if ("审批".equals(image.getAnchorLabel())) {
                image.setAnchorLabel(APPROVE_LABEL);
            } else if (!AUDIT_LABEL.equals(image.getAnchorLabel())) {
                continue;
            }
            image.setInsertInLabelRegion(true);
            images.add(image);
        }
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

    private String resolveSealTypeLabel(Integer sealType) {
        if (sealType == null) {
            return "";
        }
        String label = DictFrameworkUtils.parseDictDataLabel(SEAL_TYPE_DICT, sealType);
        return label != null ? label : String.valueOf(sealType);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : DATE_FORMATTER.format(date);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

}
