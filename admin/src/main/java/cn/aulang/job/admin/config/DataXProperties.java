package cn.aulang.job.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DataX配置
 *
 * @author wulang
 */
@Data
@ConfigurationProperties(prefix = "swater.datax")
public class DataXProperties {

    /**
     * 单通道批量处理条数
     */
    private int batchSize = 256;
    /**
     * 单通道处理速度，2M/s
     */
    private int speed = 2097152;
    /**
     * 单通道最小内存
     */
    private int xms = 256;
    /**
     * 3通道内通道最大内存
     */
    private int xmx = 1024;
    /**
     * 切分通道数
     */
    private int split = 3;
    /**
     * DataX内存堆栈日志
     */
    private String heapDumpPath = "/datax/log";

    public String buildJvmParam(int channel) {
        return String.format("-Xms%dm -Xmx%dm -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%s", xms * channel, xmx, heapDumpPath);
    }
}
