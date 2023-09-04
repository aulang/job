package cn.aulang.dataxexecutor.job;

import cn.aulang.dataxexecutor.config.DataXProperties;
import cn.aulang.dataxexecutor.model.LogStatistics;
import cn.aulang.dataxexecutor.utils.CommandBuilder;
import cn.aulang.dataxexecutor.utils.DataXResultParser;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.context.JobHelper;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.job.core.handler.ParamJobHandler;
import cn.aulang.job.core.model.DataXParam;
import cn.aulang.job.service.JobExecutorService;
import cn.aulang.job.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.FutureTask;

/**
 * DataX任务
 *
 * @author wulang
 */
@Slf4j
@Component
@EnableConfigurationProperties(DataXProperties.class)
public class DataXJob implements ParamJobHandler<DataXParam> {

    private final DataXProperties properties;

    @Autowired
    public DataXJob(DataXProperties properties) {
        this.properties = properties;
    }

    @Override
    public Class<DataXParam> getParamClass() {
        return DataXParam.class;
    }

    @Override
    public void handle(DataXParam dataXParam) {
        String jobJsonFile;

        long logId = JobHelper.getLogId();
        try {
            jobJsonFile = genJsonFile(logId, dataXParam.getJobJson());
        } catch (IOException e) {
            JobHelper.handleFail("Save job json fail!");
            JobHelper.log(e);
            return;
        }

        int exitCode;
        Thread errThread = null;
        LogStatistics logStatistics;
        try {
            String[] cmdArray = CommandBuilder.buildExeCmd(dataXParam, properties.getDataXPyPath(), jobJsonFile);

            JobHelper.log("Execute DataX command: {}", StringUtils.join(cmdArray, StringUtils.SPACE));

            // 执行命令
            Process process = Runtime.getRuntime().exec(cmdArray);

            // 获取命令进程ID
            long processId = ProcessUtils.getPId(process);

            // 设置进程ID
            JobExecutorService.setRunningJobAttribute(JobHelper.getJobId(), JobHelper.getLogId(), Constants.PID, processId);

            // 日志线程
            FutureTask<LogStatistics> futureTask = new FutureTask<>(() ->
                    DataXResultParser.analysis(new BufferedInputStream(process.getInputStream())));

            Thread futureThread = new Thread(futureTask);
            futureThread.start();

            // 错误输出线程
            errThread = new Thread(() -> {
                try {
                    DataXResultParser.analysis(new BufferedInputStream(process.getErrorStream()));
                } catch (IOException e) {
                    JobHelper.log(e);
                }
            });
            errThread.start();

            // 等待DataX进程结束
            exitCode = process.waitFor();

            // 获取输出结果
            logStatistics = futureTask.get();

            // 等待错误输出线程
            errThread.join();
        } catch (Exception e) {
            log.error("DataX execution failed", e);
            JobHelper.handleFail(e.getMessage());
            JobHelper.log(e);
            return;
        } finally {
            if (errThread != null && errThread.isAlive()) {
                errThread.interrupt();
            }

            FileUtils.deleteQuietly(new File(jobJsonFile));
        }

        if (exitCode == 0) {
            JobHelper.handleSuccess(logStatistics.toString());
        } else {
            JobHelper.handleFail("DataX execute failed with exit code: " + exitCode);
        }
    }


    private String genJsonFile(long jobId, String jobJson) throws IOException {
        String jobFile = properties.getJobDir() + jobId + ".json";
        FileUtils.write(new File(jobFile), jobJson, StandardCharsets.UTF_8);
        return jobFile;
    }

    @Override
    public String name() {
        return GlueTypeEnum.DATAX.getName();
    }

    @Override
    public String title() {
        return "数据交换任务";
    }
}
