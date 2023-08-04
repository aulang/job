package cn.aulang.job.core.enums;

/**
 * 任务执行结果代码
 *
 * @author wulang
 */
public enum HandleCodeEnum {

    RUNNING(0, "运行中"),
    SUCCESS(200, "成功"),
    FAIL(500, "失败"),
    TIMEOUT(502, "超时"),
    CANCEL(408, "取消"),
    LOST(404, "无结果");

    /**
     * 代码
     */
    private final int code;
    /**
     * 信息
     */
    private final String msg;

    HandleCodeEnum(int code, String msg) {
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
