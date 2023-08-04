package cn.aulang.job.core.handler;

import cn.aulang.job.core.context.JobHelper;

/**
 * 参数任务执行器
 *
 * @author wulang
 */
public interface ParamJobHandler<T extends IJobParam> extends IJobHandler {

    /**
     * 解析Job参数
     *
     * @return Job参数
     */
    default T getParam() throws Exception {
        return jobMapper.readValue(JobHelper.getJobParam(), getParamClass());
    }

    /**
     * 获取参数类型
     *
     * @return 参数类型
     */
    Class<T> getParamClass();

    /**
     * Job执行
     *
     * @param t 参数
     */
    void handle(T t) throws Exception;


    /**
     * 任务执行
     */
    @Override
    default void execute() {
        try {
            T t = getParam();
            // 参数检查
            t.check();
            // 执行任务
            handle(t);
        } catch (Exception e) {
            JobHelper.handleFail(e.getMessage());
            JobHelper.log(e);
        }
    }
}
