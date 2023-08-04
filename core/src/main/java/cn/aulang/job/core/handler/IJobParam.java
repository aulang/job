package cn.aulang.job.core.handler;

/**
 * Job参数接口
 *
 * @author wulang
 */
public interface IJobParam {

    /**
     * 参数检测，不通过抛出异常
     *
     * @throws IllegalArgumentException 检测不通过
     */
    default void check() throws IllegalArgumentException {

    }
}
