package cn.aulang.job.admin.enums;

/**
 * 任务调度触发类型
 *
 * @author wulang
 */
public enum TriggerTypeEnum {

    MANUAL("手动触发"),
    TIMER("自动调度"),
    RETRY("失败重试"),
    PARENT("父任务触发"),
    API("接口触发"),
    MISFIRE("过期补偿");

    private final String title;

    TriggerTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static TriggerTypeEnum match(String name, TriggerTypeEnum defaultItem) {
        for (TriggerTypeEnum item : TriggerTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }
}
