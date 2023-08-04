package cn.aulang.job.admin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 任务每日报告
 *
 * @author wulang
 */
@Data
public class JobDailyReportVO {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date triggerDay;
    private Long total = 0L;
    private Long runningCount = 0L;
    private Long successCount = 0L;
    private Long failCount = null;

    public Long getFailCount() {
        if (failCount != null) {
            return failCount;
        }

        if (total != null && runningCount != null && successCount != null) {
            failCount = total - runningCount - successCount;
            return failCount;
        }

        return (failCount = 0L);
    }
}
