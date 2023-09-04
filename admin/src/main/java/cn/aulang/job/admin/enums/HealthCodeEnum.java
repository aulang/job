package cn.aulang.job.admin.enums;

import lombok.Getter;

/**
 * 节点健康状态枚举
 *
 * @author wulang
 */
@Getter
public enum HealthCodeEnum {

    ONLINE(1, "健康"),
    UNKNOWN(0, "未知"),
    OFFLINE(-1, "离线");

    private final int code;
    private final String msg;

    HealthCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
