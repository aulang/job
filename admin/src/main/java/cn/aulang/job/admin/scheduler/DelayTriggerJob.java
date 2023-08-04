package cn.aulang.job.admin.scheduler;

import cn.aulang.job.admin.model.po.JobInfo;
import org.springframework.lang.NonNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延期触发Job
 *
 * @author wulang
 */
public record DelayTriggerJob(JobInfo jobInfo) implements Delayed {

    @Override
    public long getDelay(@NonNull TimeUnit unit) {
        // 当前次执行时间
        Long currentTriggerTime = jobInfo.getTriggerLastTime();

        if (currentTriggerTime == null) {
            return 0;
        }

        long diff = currentTriggerTime - System.currentTimeMillis();

        return (diff > 0 ? unit.convert(diff, TimeUnit.MILLISECONDS) : 0);
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        if (this == other) {
            return 0;
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
    }
}
