package cn.dh.oa.module.oa.controller.admin.bill;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.export.BillDetailExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 单据导出入口
 */
@Tag(name = "管理后台 - 单据导出")
@RestController
@RequestMapping("/oa/bill")
@Validated
public class BillExportController {

    @Resource
    private BillDetailExportService billDetailExportService;

    @GetMapping("/export-detail")
    @Operation(summary = "导出单据详情（模板）")
    public void exportDetail(
            @Parameter(description = "单据类型编码", required = true, example = "107")
            @RequestParam("billType") String billType,
            @Parameter(description = "单据 ID", required = true, example = "1")
            @RequestParam("id") Long id,
            HttpServletResponse response) throws IOException {
        OaBillTypeEnum typeEnum = OaBillTypeEnum.getByCode(billType);
        if (typeEnum == null) {
            throw ServiceExceptionUtil.invalidParamException("不支持的 billType");
        }
        billDetailExportService.export(typeEnum, id, response);
    }
}

