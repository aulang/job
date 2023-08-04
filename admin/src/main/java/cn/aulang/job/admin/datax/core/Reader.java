package cn.aulang.job.admin.datax.core;

/**
 * 读取器接口
 *
 * @author wulang
 */
public interface Reader {

    /**
     * 读取器名称
     *
     * @return 读取器名称
     */
    String getName();

    /**
     * 获取读配置参数
     *
     * @return 获取读配置参数
     */
    Parameter getParameter();
}
