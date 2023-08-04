package cn.aulang.job.admin.utils;

import cn.aulang.common.core.tools.Snowflake;

import java.util.UUID;

/**
 * ID帮助类
 *
 * @author wulang
 */
public class IdUtils {

    private static final Snowflake SNOW_FLAKE = new Snowflake();

    public static long longId() {
        return SNOW_FLAKE.nextId();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
