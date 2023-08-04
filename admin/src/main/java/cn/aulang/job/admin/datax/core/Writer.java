package cn.aulang.job.admin.datax.core;

/**
 * 写入器
 *
 * @author wulang
 */
public interface Writer {

    /**
     * 写入器名称
     *
     * @return 写入器名称
     */
    String getName();

    /**
     * 获取写配置参数
     *
     * @return 写配置参数
     */
    Parameter getParameter();
}
