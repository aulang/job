package cn.aulang.job.core.enums;

/**
 * ID自增类型
 *
 * @author wulang
 */
public enum IncrementTypeEnum {

    ID(1, "主键自增"),
    TIME(2, "时间自增");

    /**
     * 代码
     */
    private final int code;
    /**
     * 标题
     */
    private final String title;

    IncrementTypeEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public static IncrementTypeEnum match(Integer code) {
        if (code == null) {
            return null;
        }

        for (IncrementTypeEnum item : IncrementTypeEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }

        return null;
    }
}
