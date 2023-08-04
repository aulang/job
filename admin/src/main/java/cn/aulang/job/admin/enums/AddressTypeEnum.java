package cn.aulang.job.admin.enums;

/**
 * 地址类型
 *
 * @author wulang
 */
public enum AddressTypeEnum {

    AUTO(0, "自动注册"),
    MANUAL(1, "手动录入");

    private final int code;
    private final String title;

    AddressTypeEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }
}
