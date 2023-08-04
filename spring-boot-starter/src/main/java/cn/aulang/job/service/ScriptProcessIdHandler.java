package cn.aulang.job.service;

import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.context.JobHelper;
import cn.aulang.job.core.handler.ScriptProcessHandler;
import cn.aulang.job.utils.ProcessUtils;

/**
 * 脚本进程ID处理器
 *
 * @author wulang
 */
public class ScriptProcessIdHandler implements ScriptProcessHandler {

    public static final ScriptProcessHandler INSTANCE = new ScriptProcessIdHandler();

    public static ScriptProcessHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handle(Process process) {
        // 获取进程ID
        long processId = ProcessUtils.getPId(process);

        // 设置进程ID
        JobExecutorService.setRunningJobAttribute(JobHelper.getJobId(), JobHelper.getLogId(), Constants.PID, processId);
    }
}
