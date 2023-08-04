package cn.aulang.job.utils;

import com.sun.jna.Platform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

/**
 * 进程处理帮助类
 *
 * @author wulang
 */
@Slf4j
public class ProcessUtils {

    /**
     * 获取进程ID
     *
     * @param process 进程
     * @return 进程ID
     */
    public static long getPId(Process process) throws RuntimeException {
        Field field;
        if (Platform.isWindows()) {
            // java.lang.Win32Process or java.lang.ProcessImpl
            try {
                field = process.getClass().getDeclaredField("handle");
                field.setAccessible(true);
                // 句柄
                long handle = field.getLong(process);
                return Kernel32.INSTANCE.GetProcessId(handle);
            } catch (Exception e) {
                log.error("Failed to get Windows process id", e);
                throw new RuntimeException(e);
            }
        } else if (isUnixLike()) {
            // java.lang.UNIXProcess
            try {
                Class<?> clazz = Class.forName("java.lang.UNIXProcess");
                field = clazz.getDeclaredField("pid");
                field.setAccessible(true);
                return (Integer) field.get(process);
            } catch (Throwable e) {
                log.error("Failed to get Linux process id", e);
                throw new RuntimeException(e);
            }
        } else {
            log.error("Unsupported operating system");
            throw new RuntimeException("Unsupported operating system");
        }
    }

    /**
     * 杀死进程
     *
     * @param pid 进程PID
     * @return 是否成功
     */
    public static String kill(long pid) throws RuntimeException {
        String command;
        if (Platform.isWindows()) {
            command = "cmd.exe /c taskkill /F /T /PID " + pid;
        } else if (isUnixLike()) {
            command = "kill -9 " + pid;
        } else {
            log.error("Unsupported operating system");
            throw new RuntimeException("Unsupported operating system");
        }

        try {
            // 杀掉进程
            Process process = Runtime.getRuntime().exec(command);

            // 结果输出
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                IOUtils.copy(reader, builder);
            }

            return builder.toString();
        } catch (Exception e) {
            log.error("Failed to kill process, pid:" + pid, e);
            throw new RuntimeException(e);
        }
    }

    private static boolean isUnixLike() {
        return Platform.isLinux()
                || Platform.isMac()
                || Platform.isAIX()
                || Platform.isFreeBSD()
                || Platform.isSolaris();
    }
}
