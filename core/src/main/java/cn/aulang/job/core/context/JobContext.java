package cn.aulang.job.core.context;

import cn.aulang.job.core.enums.HandleCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Job执行上下文
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobContext {

    private long jobId;
    private long logId;
    private String jobParam;
    private String jobLogFileName;

    private int shardIndex;
    private int shardTotal;

    private int handleCode;
    private String handleMsg;

    public JobContext(long jobId, long logId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.logId = logId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        this.handleCode = HandleCodeEnum.SUCCESS.getCode();
    }

    /**
     * 支持子线程获取上下文
     */
    private static InheritableThreadLocal<JobContext> contextHolder = new InheritableThreadLocal<>();

    public static void setJobContext(JobContext jobContext) {
        contextHolder.set(jobContext);
    }

    public static JobContext getJobContext() {
        return contextHolder.get();
    }
}
