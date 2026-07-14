package cn.dh.oa.module.oa.service.bill.export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 在 Excel 模板中按标签文字插入签名图（同标签多人时横向均分签名区）
 */
@Component
public class BillExportSignatureInserter {

    private static final int MARGIN_PX = 2;

    public void insert(Sheet sheet, List<BillExportImage> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        Workbook workbook = sheet.getWorkbook();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper helper = workbook.getCreationHelper();
        for (Map.Entry<String, List<BillExportImage>> entry : groupByLabel(images).entrySet()) {
            Cell anchorCell = findLabelCell(sheet, entry.getKey());
            if (anchorCell == null) {
                continue;
            }
            int signCol = anchorCell.getColumnIndex() + 1;
            int signRow = anchorCell.getRowIndex();
            CellRangeAddress region = findMergedRegion(sheet, signRow, signCol);
            if (region == null) {
                region = new CellRangeAddress(signRow, signRow, signCol, signCol);
            }
            insertIntoRegion(sheet, drawing, helper, workbook, region, entry.getValue());
        }
    }

    private Map<String, List<BillExportImage>> groupByLabel(List<BillExportImage> images) {
        Map<String, List<BillExportImage>> grouped = new LinkedHashMap<>();
        for (BillExportImage image : images) {
            if (image.getData() == null || image.getData().length == 0
                    || image.getAnchorLabel() == null || image.getAnchorLabel().isBlank()) {
                continue;
            }
            grouped.computeIfAbsent(image.getAnchorLabel(), key -> new ArrayList<>()).add(image);
        }
        return grouped;
    }

    private void insertIntoRegion(Sheet sheet, Drawing<?> drawing, CreationHelper helper, Workbook workbook,
                                  CellRangeAddress region, List<BillExportImage> images) {
        int count = images.size();
        int regionWidth = calcRegionWidthPx(sheet, region);
        int regionHeight = calcRegionHeightPx(sheet, region);
        int slotWidth = Math.max(1, regionWidth / count);
        for (int i = 0; i < count; i++) {
            BillExportImage image = images.get(i);
            int left = i * slotWidth + MARGIN_PX;
            int right = (i == count - 1) ? regionWidth - MARGIN_PX : (i + 1) * slotWidth - MARGIN_PX;
            int top = MARGIN_PX;
            int bottom = regionHeight - MARGIN_PX;
            int pictureIdx = workbook.addPicture(image.getData(), image.getPictureType());
            ClientAnchor anchor = createAnchorForRect(sheet, helper, region, left, top, right, bottom);
            drawing.createPicture(anchor, pictureIdx);
        }
    }

    private ClientAnchor createAnchorForRect(Sheet sheet, CreationHelper helper, CellRangeAddress region,
                                             int leftPx, int topPx, int rightPx, int bottomPx) {
        ClientAnchor anchor = helper.createClientAnchor();
        int col1 = region.getFirstColumn();
        int dx1 = leftPx;
        int x = 0;
        for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
            int colW = Math.round(sheet.getColumnWidthInPixels(col));
            if (leftPx < x + colW || col == region.getLastColumn()) {
                col1 = col;
                dx1 = leftPx - x;
                break;
            }
            x += colW;
        }
        int col2 = region.getLastColumn();
        int dx2 = rightPx;
        x = 0;
        for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
            int colW = Math.round(sheet.getColumnWidthInPixels(col));
            if (rightPx <= x + colW || col == region.getLastColumn()) {
                col2 = col;
                dx2 = rightPx - x;
                break;
            }
            x += colW;
        }
        int row1 = region.getFirstRow();
        int dy1 = topPx;
        int y = 0;
        for (int rowIdx = region.getFirstRow(); rowIdx <= region.getLastRow(); rowIdx++) {
            int rowH = rowHeightPx(sheet, rowIdx);
            if (topPx < y + rowH || rowIdx == region.getLastRow()) {
                row1 = rowIdx;
                dy1 = topPx - y;
                break;
            }
            y += rowH;
        }
        int row2 = region.getLastRow();
        int dy2 = bottomPx;
        y = 0;
        for (int rowIdx = region.getFirstRow(); rowIdx <= region.getLastRow(); rowIdx++) {
            int rowH = rowHeightPx(sheet, rowIdx);
            if (bottomPx <= y + rowH || rowIdx == region.getLastRow()) {
                row2 = rowIdx;
                dy2 = bottomPx - y;
                break;
            }
            y += rowH;
        }
        anchor.setCol1(col1);
        anchor.setRow1(row1);
        anchor.setCol2(col2);
        anchor.setRow2(row2);
        anchor.setDx1(Units.pixelToEMU(dx1));
        anchor.setDy1(Units.pixelToEMU(dy1));
        anchor.setDx2(Units.pixelToEMU(dx2));
        anchor.setDy2(Units.pixelToEMU(dy2));
        return anchor;
    }

    private int calcRegionWidthPx(Sheet sheet, CellRangeAddress region) {
        float widthPx = 0;
        for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
            widthPx += sheet.getColumnWidthInPixels(col);
        }
        return Math.round(widthPx);
    }

    private int calcRegionHeightPx(Sheet sheet, CellRangeAddress region) {
        float heightPx = 0;
        for (int rowIdx = region.getFirstRow(); rowIdx <= region.getLastRow(); rowIdx++) {
            heightPx += rowHeightPx(sheet, rowIdx);
        }
        return Math.round(heightPx);
    }

    private int rowHeightPx(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        float heightPt = row == null || row.getHeightInPoints() <= 0
                ? sheet.getDefaultRowHeightInPoints()
                : row.getHeightInPoints();
        return Math.round(Units.pointsToPixel(heightPt));
    }

    private CellRangeAddress findMergedRegion(Sheet sheet, int row, int col) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.isInRange(row, col)) {
                return region;
            }
        }
        return null;
    }

    private Cell findLabelCell(Sheet sheet, String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        for (Row row : sheet) {
            if (row == null) {
                continue;
            }
            for (Cell cell : row) {
                if (label.equals(readCellString(cell))) {
                    return cell;
                }
            }
        }
        return null;
    }

    private String readCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

}
