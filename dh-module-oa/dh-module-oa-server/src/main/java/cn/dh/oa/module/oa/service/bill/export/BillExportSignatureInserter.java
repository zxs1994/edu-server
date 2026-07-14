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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 在 Excel 模板中按标签文字插入签名图（同标签多人时横向均分签名区；单人按原图比例适配，不拉伸）
 */
@Component
public class BillExportSignatureInserter {

    private static final int MARGIN_PX = 2;
    /** 单人签名最大宽度，避免宽合并区把签名拉得过大 */
    private static final int MAX_SINGLE_SIGN_WIDTH_PX = 120;

    public void insert(Sheet sheet, List<BillExportImage> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        Workbook workbook = sheet.getWorkbook();
        // 模板/克隆 sheet 可能已有 drawing，重复 create 会导致 xlsx 损坏
        Drawing<?> drawing = sheet.getDrawingPatriarch();
        if (drawing == null) {
            drawing = sheet.createDrawingPatriarch();
        }
        CreationHelper helper = workbook.getCreationHelper();
        for (Map.Entry<String, List<BillExportImage>> entry : groupByLabel(images).entrySet()) {
            Cell anchorCell = findLabelCell(sheet, entry.getKey());
            if (anchorCell == null) {
                continue;
            }
            boolean inLabelRegion = entry.getValue().stream().anyMatch(BillExportImage::isInsertInLabelRegion);
            CellRangeAddress labelRegion = findMergedRegion(sheet, anchorCell.getRowIndex(), anchorCell.getColumnIndex());
            int signCol;
            if (inLabelRegion) {
                signCol = anchorCell.getColumnIndex();
            } else if (labelRegion != null) {
                // 标签在合并格内时，签名区取合并区右侧第一列（如 H19:I19 标签 → J19 签名）
                signCol = labelRegion.getLastColumn() + 1;
            } else {
                signCol = anchorCell.getColumnIndex() + 1;
            }
            int signRow = anchorCell.getRowIndex();
            CellRangeAddress region = findMergedRegion(sheet, signRow, signCol);
            if (region == null) {
                region = new CellRangeAddress(signRow, signRow, signCol, signCol);
            }
            int leftReservePx = inLabelRegion ? 72 : 0;
            insertIntoRegion(sheet, drawing, helper, workbook, region, entry.getValue(), leftReservePx);
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
                                  CellRangeAddress region, List<BillExportImage> images, int leftReservePx) {
        int count = images.size();
        int regionWidth = Math.max(1, calcRegionWidthPx(sheet, region));
        int regionHeight = Math.max(1, calcRegionHeightPx(sheet, region));
        int usableWidth = Math.max(1, regionWidth - leftReservePx);
        int slotWidth = Math.max(1, usableWidth / count);
        for (int i = 0; i < count; i++) {
            BillExportImage image = images.get(i);
            int[] drawSize = calcFitSize(image.getData(), slotWidth, regionHeight, count == 1, image);
            int drawW = drawSize[0];
            int drawH = drawSize[1];
            int left = leftReservePx + i * slotWidth + MARGIN_PX;
            int top = MARGIN_PX + Math.max(0, (regionHeight - 2 * MARGIN_PX - drawH) / 2);
            int right = Math.min(regionWidth - MARGIN_PX, left + drawW);
            int bottom = Math.min(regionHeight - MARGIN_PX, top + drawH);
            if (right <= left || bottom <= top) {
                continue;
            }
            int pictureIdx = workbook.addPicture(image.getData(), image.getPictureType());
            ClientAnchor anchor = createAnchorForRect(sheet, helper, region, left, top, right, bottom);
            drawing.createPicture(anchor, pictureIdx);
        }
    }

    private int[] calcFitSize(byte[] imageData, int slotWidth, int regionHeight, boolean single,
                              BillExportImage image) {
        int maxW = Math.max(1, slotWidth - 2 * MARGIN_PX);
        int maxH = Math.max(1, regionHeight - 2 * MARGIN_PX);
        if (single) {
            Integer customMaxW = image.getMaxSignWidthPx();
            if (customMaxW != null && customMaxW == 0) {
                // 不限制宽度，铺满签名区
            } else if (customMaxW != null) {
                maxW = Math.min(maxW, customMaxW);
            } else {
                maxW = Math.min(maxW, MAX_SINGLE_SIGN_WIDTH_PX);
            }
        }
        int[] natural = readImageSize(imageData);
        int natW = natural[0];
        int natH = natural[1];
        double scaleCap = image.isAllowUpscale() ? Double.MAX_VALUE : 1.0;
        double scale = Math.min(scaleCap, Math.min((double) maxW / natW, (double) maxH / natH));
        return new int[]{
                Math.max(1, (int) Math.round(natW * scale)),
                Math.max(1, (int) Math.round(natH * scale))
        };
    }

    private int[] readImageSize(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image != null && image.getWidth() > 0 && image.getHeight() > 0) {
                return new int[]{image.getWidth(), image.getHeight()};
            }
        } catch (Exception ignored) {
            // 读失败时用默认比例
        }
        return new int[]{150, 60};
    }

    private ClientAnchor createAnchorForRect(Sheet sheet, CreationHelper helper, CellRangeAddress region,
                                             int leftPx, int topPx, int rightPx, int bottomPx) {
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);

        int[] startCol = resolveColumnOffset(sheet, region, leftPx);
        int[] endCol = resolveColumnOffset(sheet, region, rightPx);
        int[] startRow = resolveRowOffset(sheet, region, topPx);
        int[] endRow = resolveRowOffset(sheet, region, bottomPx);

        int col1 = startCol[0];
        int dx1 = startCol[1];
        int col2 = endCol[0];
        int dx2 = endCol[1];
        int row1 = startRow[0];
        int dy1 = startRow[1];
        int row2 = endRow[0];
        int dy2 = endRow[1];

        // 防止同格内终点偏移 <= 起点，或偏移超出单元格，避免 OOXML 损坏
        int col1W = Math.max(2, Math.round(sheet.getColumnWidthInPixels(col1)));
        int col2W = Math.max(2, Math.round(sheet.getColumnWidthInPixels(col2)));
        int row1H = Math.max(2, rowHeightPx(sheet, row1));
        int row2H = Math.max(2, rowHeightPx(sheet, row2));
        dx1 = clamp(dx1, 0, col1W - 1);
        dy1 = clamp(dy1, 0, row1H - 1);
        dx2 = clamp(dx2, 0, col2W);
        dy2 = clamp(dy2, 0, row2H);
        if (col1 == col2 && dx2 <= dx1) {
            dx2 = Math.min(col2W, dx1 + 1);
        }
        if (row1 == row2 && dy2 <= dy1) {
            dy2 = Math.min(row2H, dy1 + 1);
        }
        if (col2 < col1 || (col2 == col1 && row2 < row1)) {
            col2 = col1;
            row2 = row1;
            dx2 = Math.min(col1W, dx1 + 1);
            dy2 = Math.min(row1H, dy1 + 1);
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

    /** @return [colIndex, offsetPxWithinCol] */
    private int[] resolveColumnOffset(Sheet sheet, CellRangeAddress region, int offsetFromRegionLeftPx) {
        int x = 0;
        for (int col = region.getFirstColumn(); col <= region.getLastColumn(); col++) {
            int colW = Math.max(1, Math.round(sheet.getColumnWidthInPixels(col)));
            if (offsetFromRegionLeftPx < x + colW || col == region.getLastColumn()) {
                return new int[]{col, Math.max(0, offsetFromRegionLeftPx - x)};
            }
            x += colW;
        }
        return new int[]{region.getLastColumn(), 0};
    }

    /** @return [rowIndex, offsetPxWithinRow] */
    private int[] resolveRowOffset(Sheet sheet, CellRangeAddress region, int offsetFromRegionTopPx) {
        int y = 0;
        for (int rowIdx = region.getFirstRow(); rowIdx <= region.getLastRow(); rowIdx++) {
            int rowH = Math.max(1, rowHeightPx(sheet, rowIdx));
            if (offsetFromRegionTopPx < y + rowH || rowIdx == region.getLastRow()) {
                return new int[]{rowIdx, Math.max(0, offsetFromRegionTopPx - y)};
            }
            y += rowH;
        }
        return new int[]{region.getLastRow(), 0};
    }

    private int clamp(int value, int min, int max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
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
        String normalizedLabel = normalizeLabel(label);
        for (Row row : sheet) {
            if (row == null) {
                continue;
            }
            for (Cell cell : row) {
                String cellText = readCellString(cell);
                if (cellText == null) {
                    continue;
                }
                if (normalizedLabel.equals(normalizeLabel(cellText))) {
                    return cell;
                }
            }
        }
        return null;
    }

    private String normalizeLabel(String text) {
        String value = text.trim();
        if (value.endsWith("：") || value.endsWith(":")) {
            value = value.substring(0, value.length() - 1).trim();
        }
        return value;
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
