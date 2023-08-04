package cn.aulang.job.admin.enums;

/**
 * 路由策略
 *
 * @author wulang
 */
public enum RouteStrategyEnum {

    FIRST("第一个"),
    LAST("最后一个"),
    RANDOM("随机"),
    ROUND("轮询"),
    HASH("一致性HASH"),
    LFU("最不经常使用"),
    LRU("最近最久未使用"),
    FAILOVER("故障转移"),
    BUSY_OVER("忙碌转移"),
    BROADCAST("广播模式");

    RouteStrategyEnum(String title) {
        this.title = title;
    }

    private final String title;

    public String getTitle() {
        return title;
    }

    public static RouteStrategyEnum match(String name, RouteStrategyEnum defaultItem) {
        if (name != null) {
            for (RouteStrategyEnum item : RouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
