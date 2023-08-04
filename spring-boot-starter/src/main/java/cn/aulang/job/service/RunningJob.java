package cn.aulang.job.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 运行中的任务
 *
 * @author wulang
 */
public class RunningJob {

    private long jobId;
    private long logId;
    private long logDateTime;
    private boolean isScript;
    private Future<?> future;

    private Map<String, Object> attributes;

    public RunningJob(long jobId, long logId, long logDateTime, boolean isScript,  Future<?> future) {
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

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(long logDateTime) {
        this.logDateTime = logDateTime;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public boolean isScript() {
        return isScript;
    }

    public void setScript(boolean script) {
        isScript = script;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
