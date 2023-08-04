package cn.aulang.job.core.utils;

import cn.aulang.job.core.handler.ScriptProcessHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 脚本执行帮助类
 *
 * @author wulang
 */
@Slf4j
public class ScriptUtils {

    /**
     * 构造执行命令
     *
     * @param command    脚本执行命令
     * @param scriptFile 脚本文件
     * @param params     参数
     * @return 执行命令
     */
    public static String[] buildCmdArray(String command, String scriptFile, String... params) {
        List<String> cmdList = new ArrayList<>();
        cmdList.add(command);
        cmdList.add(scriptFile);

        if (params != null && params.length > 0) {
            cmdList.addAll(Arrays.asList(params));
        }

        return cmdList.toArray(new String[0]);
    }

    /**
     * 执行脚本
     *
     * @param command    脚本执行命令
     * @param scriptFile 脚本文件
     * @param logFile    日志文件
     * @param handler    可选进程处理器，对进程进行额外的处理
     * @param params     参数
     * @return 执行结果
     */
    public static int exec(String command, String scriptFile, String logFile, ScriptProcessHandler handler, String... params)
            throws Exception {
        Thread inputThread = null;
        Thread errThread = null;

        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            // 构造执行命令
            String[] cmdArray = buildCmdArray(command, scriptFile, params);

            // 执行命令
            Process process = Runtime.getRuntime().exec(cmdArray);

            if (handler != null) {
                handler.handle(process);
            }

            // 进程输出线程
            inputThread = new Thread(() -> {
                try {
                    IOUtils.copy(process.getInputStream(), fos);
                } catch (IOException e) {
                    log.error("Fail to read process output log", e);
                }
            });
            // 启动线程
            inputThread.start();

            // 进程错误输出线程
            errThread = new Thread(() -> {
                try {
                    IOUtils.copy(process.getErrorStream(), fos);
                } catch (IOException e) {
                    log.error("Fail to read process error log", e);
                }
            });
            // 启动线程
            errThread.start();

            // 等待执行结果
            int exitCode = process.waitFor();

            // 等待读取进程输出
            inputThread.join();
            errThread.join();

            // 返回执行结果
            return exitCode;
        } finally {
            // 异常中段线程
            interrupt(inputThread);
            interrupt(errThread);
        }
    }

    public static void interrupt(Thread thread) {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
