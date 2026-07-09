package cn.dh.oa.module.oa.service.bill.export;

import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 单据详情导出处理器
 */
public interface BillDetailExportHandler {

    /**
     * 支持的单据类型
     */
    OaBillTypeEnum supportBillType();

    /**
     * 导出单据详情
     */
    void export(Long id, HttpServletResponse response) throws IOException;
}

