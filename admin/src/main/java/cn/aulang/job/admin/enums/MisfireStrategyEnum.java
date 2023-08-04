package cn.aulang.job.admin.enums;

/**
 * 调度过期策略
 *
 * @author wulang
 */
public enum MisfireStrategyEnum {
    /**
     * 无操作
     */
    DO_NOTHING("忽略"),
    /**
     * 立即执行一次
     */
    FIRE_ONCE_NOW("立即执行一次");

    private final String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem) {
        for (MisfireStrategyEnum item : MisfireStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }
}
