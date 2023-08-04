package cn.aulang.job.core.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任务执行触发参数
 *
 * @author wulang
 */
@Data
public class TriggerParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private long jobId;

    /**
     * 任务类型
     */
    private String glueType;

    /**
     * 处理器
     */
    private String handler;
    /**
     * 处理器参数
     */
    private String handlerParam;

    /**
     * 脚本代码源
     */
    private String glueSource;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 日志ID
     */
    private long logId;
    /**
     * 日志时间
     */
    private long logDateTime;
    /**
     * 上次执行时间
     */
    private long lastDateTime;

    /**
     * 当前分片索引
     */
    private int shardIndex;
    /**
     * 总分片数
     */
    private int shardTotal;
}
