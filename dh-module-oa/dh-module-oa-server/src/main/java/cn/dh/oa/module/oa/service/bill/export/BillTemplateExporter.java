package cn.dh.oa.module.oa.service.bill.export;

import cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil;
import cn.dh.oa.framework.common.util.http.HttpUtils;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 单据模板导出引擎（FastExcel fill）
 */
@Component
public class BillTemplateExporter {

    private static final FillConfig DETAIL_FILL_CONFIG = FillConfig.builder().forceNewRow(false).build();

    @Resource
    private BillExportSignatureInserter billExportSignatureInserter;

    /**
     * 导出（支持多 sheet 分页，每页数据独立填充）
     */
    public void export(HttpServletResponse response, String templateClasspath, String fileName,
                       List<BillExportData> pages) throws IOException {
        if (pages == null || pages.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("导出数据为空");
        }
        ClassPathResource template = new ClassPathResource(templateClasspath);
        if (!template.exists()) {
            throw ServiceExceptionUtil.invalidParamException("导出模板不存在: " + templateClasspath);
        }

        byte[] fileBytes = buildWorkbookBytes(template, pages);
        writeExcelResponse(response, fileName, fileBytes);
    }

    private byte[] buildWorkbookBytes(ClassPathResource template, List<BillExportData> pages) throws IOException {
        byte[] multiSheetTemplate = buildMultiSheetTemplate(template, pages.size());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream templateIn = new ByteArrayInputStream(multiSheetTemplate);
             ExcelWriter writer = FastExcelFactory.write(buffer)
                     .withTemplate(templateIn)
                     .build()) {
            for (int i = 0; i < pages.size(); i++) {
                WriteSheet sheet = pages.size() == 1
                        ? FastExcelFactory.writerSheet().build()
                        : FastExcelFactory.writerSheet(i, "报销单-" + (i + 1)).build();
                fillSheet(writer, sheet, pages.get(i));
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.invalidParamException("导出失败: " + e.getMessage());
        }
        return insertSignatures(buffer.toByteArray(), pages);
    }

    private byte[] insertSignatures(byte[] filledBytes, List<BillExportData> pages) throws IOException {
        boolean hasSignature = pages.stream().anyMatch(page -> page.getSignatureImages() != null
                && !page.getSignatureImages().isEmpty());
        if (!hasSignature) {
            return filledBytes;
        }
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(filledBytes));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (int i = 0; i < pages.size(); i++) {
                BillExportData page = pages.get(i);
                if (page.getSignatureImages() == null || page.getSignatureImages().isEmpty()) {
                    continue;
                }
                Sheet sheet = workbook.getSheetAt(i);
                billExportSignatureInserter.insert(sheet, page.getSignatureImages());
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void writeExcelResponse(HttpServletResponse response, String fileName, byte[] fileBytes)
            throws IOException {
        response.resetBuffer();
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(fileName));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try (OutputStream output = response.getOutputStream()) {
            output.write(fileBytes);
            output.flush();
        }
    }

    private void fillSheet(ExcelWriter writer, WriteSheet sheet, BillExportData data) {
        Map<String, Object> mainFields = data.getMainFields();
        if (mainFields != null && !mainFields.isEmpty()) {
            writer.fill(mainFields, sheet);
        }
        List<Map<String, Object>> detailList = data.getDetailList();
        if (detailList != null && !detailList.isEmpty()) {
            FillConfig fillConfig = Boolean.TRUE.equals(data.getDetailForceNewRow())
                    ? FillConfig.builder().forceNewRow(true).build()
                    : DETAIL_FILL_CONFIG;
            writer.fill(detailList, fillConfig, sheet);
        }
    }

    private byte[] buildMultiSheetTemplate(ClassPathResource template, int sheetCount) throws IOException {
        try (InputStream in = template.getInputStream();
             Workbook workbook = WorkbookFactory.create(in);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (int i = 1; i < sheetCount; i++) {
                workbook.cloneSheet(0);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

}
