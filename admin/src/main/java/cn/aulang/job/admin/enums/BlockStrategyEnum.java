package cn.aulang.job.admin.enums;

/**
 * 阻塞处理策略
 *
 * @author wulang
 */
public enum BlockStrategyEnum {

    SERIAL_EXECUTION("单机串行"),
    PARALLEL_EXECUTION("多机并行"),
    DISCARD_LATER("丢弃后续调度"),
    COVER_EARLY("覆盖之前调度");

    private final String title;

    BlockStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static BlockStrategyEnum match(String name, BlockStrategyEnum defaultItem) {
        for (BlockStrategyEnum item : BlockStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }
}
