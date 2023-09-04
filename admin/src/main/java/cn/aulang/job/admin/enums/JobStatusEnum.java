package cn.aulang.job.admin.enums;

import lombok.Getter;

/**
 * 任务调度状态枚举
 *
 * @author wulang
 */
@Getter
public enum JobStatusEnum {

    STOP(0, "停止"),
    RUNNING(1, "运行");

    private final int code;
    private final String title;

    JobStatusEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }
}
