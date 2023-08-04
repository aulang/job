package cn.aulang.job.core.handler.impl;

import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.context.JobHelper;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.job.core.handler.IJobHandler;
import cn.aulang.job.core.handler.ScriptProcessHandler;
import cn.aulang.job.core.log.JobFileAppender;
import cn.aulang.job.core.utils.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 脚本任务处理器
 *
 * @author wulang
 */
@Slf4j
public class ScriptJobHandler implements IJobHandler {

    /**
     * 任务ID
     */
    protected final long jobId;
    /**
     * 代码类型
     */
    protected final GlueTypeEnum glueType;
    /**
     * 代码内容
     */
    protected final String glueSource;

    /**
     * 可选脚本进程处理器
     */
    protected final ScriptProcessHandler processHandler;


    public ScriptJobHandler(long jobId, GlueTypeEnum glueType, String glueSource) {
        this(jobId, glueType, glueSource, null);
    }

    public ScriptJobHandler(long jobId, GlueTypeEnum glueType, String glueSource, ScriptProcessHandler processHandler) {
        this.jobId = jobId;
        this.glueType = glueType;
        this.glueSource = glueSource;
        this.processHandler = processHandler;
    }

    @Override
    public void execute() throws Exception {
        if (!glueType.isScript()) {
            JobHelper.handleFail("Glue type: [" + glueType + "] is not script!");
            return;
        }

        // 脚本文件
        String scriptFileName = JobFileAppender.getGlueSrcPath()
                .concat(Constants.SPLIT_DIVIDE)
                .concat(String.valueOf(jobId))
                .concat(glueType.getSuffix());

        File scriptFile = new File(scriptFileName);
        // 覆盖写
        FileUtils.writeStringToFile(scriptFile, glueSource, StandardCharsets.UTF_8, false);

        // 日志文件
        String logFileName = JobHelper.getJobLogFileName();

        // params：0=命令参数、1=分片序号、2=分片总数
        String[] scriptParams = new String[3];
        String param = JobHelper.getJobParam();
        scriptParams[0] = StringUtils.isNotBlank(param) ? param : StringUtils.EMPTY;
        scriptParams[1] = String.valueOf(JobHelper.getShardIndex());
        scriptParams[2] = String.valueOf(JobHelper.getShardTotal());

        // 脚本执行命令
        String cmd = glueType.getCmd();

        // 执行脚本
        execScript(cmd, scriptFileName, logFileName, scriptParams);

        // 删除脚本文件
        FileUtils.deleteQuietly(scriptFile);
    }

    protected void execScript(String command, String scriptFile, String logFile, String... params) {
        // 执行脚本
        int exitCode;
        try {
            exitCode = ScriptUtils.exec(command, scriptFile, logFile, processHandler, params);
        } catch (Exception e) {
            log.error("Script execution failed", e);
            JobHelper.handleFail(e.getMessage());
            JobHelper.log(e);
            return;
        }

        if (exitCode == 0) {
            // 执行成功
            JobHelper.handleSuccess();
        } else {
            // 执行失败
            JobHelper.handleFail("script execute failed with exit code: " + exitCode);
        }
    }
}
