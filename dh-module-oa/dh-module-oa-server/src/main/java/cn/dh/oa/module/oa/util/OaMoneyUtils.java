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
        String[] fraction = {"角", "分"};
        String[] digit = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[][] unit = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};
        BigDecimal n = amount.abs().setScale(2, RoundingMode.HALF_UP);

        StringBuilder s = new StringBuilder();
        int jiao = n.movePointRight(1).intValue() % 10;
        int fen = n.movePointRight(2).intValue() % 10;
        s.append((jiao == 0 ? "" : digit[jiao] + fraction[0]))
                .append(fen == 0 ? "" : digit[fen] + fraction[1]);
        if (s.length() == 0) {
            s = new StringBuilder("整");
        }
        int integerPart = n.intValue();
        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            StringBuilder p = new StringBuilder();
            for (int j = 0; j < unit[1].length && integerPart > 0; j++) {
                int num = integerPart % 10;
                p.insert(0, num == 0 ? "零" : digit[num] + unit[1][j]);
                integerPart = integerPart / 10;
            }
            String section = p.toString()
                    .replaceAll("(零.)*零$", "")
                    .replaceAll("^$", "零");
            s.insert(0, section + unit[0][i]);
        }
        String result = s.toString()
                .replaceAll("(零.)*零元", "元")
                .replaceAll("(零.)+", "零")
                .replaceAll("^整$", "零元整");
        if (!result.endsWith("分") && !result.endsWith("角")) {
            result = result + "整";
        }
        return result;
    }

}
