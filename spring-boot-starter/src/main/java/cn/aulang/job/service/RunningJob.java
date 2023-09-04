package cn.aulang.job.service;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 运行中的任务
 *
 * @author wulang
 */
@Getter
@Setter
public class RunningJob {

    private long jobId;
    private long logId;
    private long logDateTime;
    private boolean isScript;
    private Future<?> future;

    private Map<String, Object> attributes;

    public RunningJob(long jobId, long logId, long logDateTime, boolean isScript, Future<?> future) {
        this.jobId = jobId;
        this.logId = logId;
        this.isScript = isScript;
        this.logDateTime = logDateTime;
        this.future = future;
    }

    public boolean setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(key, value);

        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        if (attributes == null) {
            return null;
        }

        return (T) attributes.get(key);
    }
}
