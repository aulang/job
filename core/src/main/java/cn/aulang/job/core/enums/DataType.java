package cn.aulang.job.core.enums;

import lombok.Getter;

/**
 * 数据类型
 *
 * @author wulang
 */
@Getter
public enum DataType {

    INTEGER("Integer", "整型"),
    FLOAT("Float", "浮点型"),
    BOOLEAN("Boolean", "布尔型"),
    STRING("String", "字符串"),
    DATE("Date", "日期"),
    UNKNOWN("Unknown", "未支持");

    /**
     * 代码
     */
    private final String code;
    /**
     * 标题
     */
    private final String title;

    DataType(String code, String title) {
        this.code = code;
        this.title = title;
    }
}
