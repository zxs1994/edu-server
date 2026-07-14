package cn.dh.oa.module.oa.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * OA 金额格式化工具
 */
public final class OaMoneyUtils {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    private OaMoneyUtils() {
    }

    public static String formatWithComma(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return MONEY_FORMAT.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    /** 导出用金额数值（保留 2 位小数，供 Excel 单元格货币格式渲染） */
    public static BigDecimal toMoneyValue(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /** 人民币金额文字版：￥1,234.56 */
    public static String formatRmbText(BigDecimal amount) {
        String formatted = formatWithComma(amount);
        return formatted.isEmpty() ? "" : "￥" + formatted;
    }

    /**
     * 人民币金额转中文大写
     */
    public static String toChineseUpper(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "零元整";
        }
        String[] digit = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[] unit = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿"};
        BigDecimal n = amount.abs().setScale(2, RoundingMode.HALF_UP);
        long yuan = n.longValue();
        int jiao = n.movePointRight(1).remainder(BigDecimal.TEN).intValue();
        int fen = n.movePointRight(2).remainder(BigDecimal.TEN).intValue();

        StringBuilder sb = new StringBuilder();
        if (yuan > 0) {
            String yuanStr = String.valueOf(yuan);
            int len = yuanStr.length();
            boolean needZero = false;
            for (int i = 0; i < len; i++) {
                int num = yuanStr.charAt(i) - '0';
                int pos = len - i - 1; // 当前位相对个位的偏移
                if (num == 0) {
                    needZero = true;
                    // 万、亿位即使为 0，单位也要保留（如 10000 → 壹万元）
                    if (pos == 4 || pos == 8) {
                        sb.append(unit[pos]);
                        needZero = false;
                    }
                } else {
                    if (needZero) {
                        sb.append("零");
                        needZero = false;
                    }
                    sb.append(digit[num]).append(unit[pos]);
                }
            }
            if (!sb.toString().endsWith("元")) {
                sb.append("元");
            }
        } else {
            sb.append("零元");
        }

        if (jiao == 0 && fen == 0) {
            sb.append("整");
        } else {
            if (jiao > 0) {
                sb.append(digit[jiao]).append("角");
            } else if (fen > 0 && yuan > 0) {
                sb.append("零");
            }
            if (fen > 0) {
                sb.append(digit[fen]).append("分");
            }
        }
        return sb.toString();
    }

}
