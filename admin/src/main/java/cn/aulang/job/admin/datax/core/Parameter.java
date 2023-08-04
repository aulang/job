package cn.aulang.job.admin.datax.core;

/**
 * 参数接口
 *
 * @author wulang
 */
public interface Parameter {

    /**
     * 参数检查，不合法抛出异常
     *
     * @throws IllegalArgumentException 参数不合法
     */
    default void check() throws IllegalArgumentException {

    }
}
