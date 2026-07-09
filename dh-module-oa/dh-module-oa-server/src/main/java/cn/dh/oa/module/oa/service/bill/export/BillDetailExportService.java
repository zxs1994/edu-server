package cn.dh.oa.module.oa.service.bill.export;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 单据详情导出服务
 */
@Service
public class BillDetailExportService {

    private final Map<OaBillTypeEnum, BillDetailExportHandler> handlerMap = new EnumMap<>(OaBillTypeEnum.class);

    @Resource
    public void setHandlers(List<BillDetailExportHandler> handlers) {
        for (BillDetailExportHandler handler : handlers) {
            handlerMap.put(handler.supportBillType(), handler);
        }
    }

    public void export(OaBillTypeEnum billType, Long id, HttpServletResponse response) throws IOException {
        BillDetailExportHandler handler = handlerMap.get(billType);
        if (handler == null) {
            throw ServiceExceptionUtil.invalidParamException("该单据类型暂未实现详情导出");
        }
        handler.export(id, response);
    }
}

