package cn.dh.oa.module.oa.service.bill.export;

import lombok.Data;

/**
 * 单据导出签名图（按模板标签定位）
 */
@Data
public class BillExportImage {

    /** 模板中用于定位的标签文字，如「审核」「审批」「批准」「印鉴管理人」 */
    private String anchorLabel;

    private byte[] data;

    /** Apache POI 图片类型，如 Workbook.PICTURE_TYPE_JPEG */
    private int pictureType;

    /**
     * true：签名插入标签所在合并区右侧（标签与签名同格）；
     * false：插入标签右侧一列（默认，差旅/接待等）
     */
    private boolean insertInLabelRegion;

    /** 单人签名最大宽度（px）；null 默认 120；0 表示不限制，铺满签名区 */
    private Integer maxSignWidthPx;

    /** true 时允许放大签名图以适配签名区（领款人等宽区签名） */
    private boolean allowUpscale;

}
