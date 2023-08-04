package cn.aulang.job.core.handler;

/**
 * 脚本进程处理器
 */
@FunctionalInterface
public interface ScriptProcessHandler {

    /**
     * 对脚本进程进行处理
     *
     * @param process 脚本进程
     */
    void handle(Process process) throws Exception;
}
