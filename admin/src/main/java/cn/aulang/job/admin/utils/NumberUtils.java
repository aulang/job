package cn.aulang.job.admin.utils;

/**
 * 数字帮助类
 *
 * @author wulang
 */
public class NumberUtils {

    public static boolean isInteger(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parseInt(String str) {
        return Integer.parseInt(str);
    }

    public static Integer parseInt(String str, Integer defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean isLong(String str) {
        try {
            Long.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotLong(String str) {
        try {
            Long.valueOf(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static long parseLong(String str) {
        return Long.parseLong(str);
    }

    public static Long parseLong(String str, Long defaultValue) {
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double parseDouble(String str) {
        return Double.parseDouble(str);
    }

    public static Double parseDouble(String str, Double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
