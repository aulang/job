package cn.aulang.job.admin.enums;

import lombok.Getter;

/**
 * 调度类型
 *
 * @author wulang
 */
@Getter
public enum ScheduleTypeEnum {

    CRON("CRON"),
    FIX_RATE("固定速度");

    private final String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem) {
        for (ScheduleTypeEnum item : ScheduleTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }
}
