package cn.dh.oa.module.oa.service.bill.export;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 单据模板导出数据
 */
@Data
public class BillExportData {

    /** 主表字段，模板占位符 {字段名} */
    private Map<String, Object> mainFields;

    /** 明细列表，模板占位符 {.字段名} */
    private List<Map<String, Object>> detailList;

}
