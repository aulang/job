package cn.aulang.job.core.enums;

/**
 * 任务触发结果代码
 *
 * @author wulang
 */
public enum TriggerCodeEnum {

    SUCCESS(200, "success"),
    FAIL(500, "fail");

    /**
     * 代码
     */
    private final int code;
    /**
     * 信息
     */
    private final String msg;

    TriggerCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
