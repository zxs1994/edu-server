package cn.dh.oa.module.oa.service.bill.export;

import lombok.Data;

/**
 * 单据导出签名图（按模板标签定位）
 */
@Data
public class BillExportImage {

    /** 模板中用于定位的标签文字，如「审核」「审批」 */
    private String anchorLabel;

    private byte[] data;

    /** Apache POI 图片类型，如 Workbook.PICTURE_TYPE_JPEG */
    private int pictureType;

}
