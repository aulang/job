package cn.aulang.job.admin.service;

import cn.aulang.job.admin.dao.JobInfoDao;
import cn.aulang.job.admin.dao.JobReportDao;
import cn.aulang.job.admin.model.po.JobReport;
import cn.aulang.job.admin.model.vo.JobCountVO;
import cn.aulang.job.admin.model.vo.JobDailyCountVO;
import cn.aulang.job.admin.model.vo.JobStatusCountVO;
import cn.aulang.common.core.utils.DatePattern;
import cn.aulang.common.core.utils.SimpleDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 统计预览服务
 *
 * @author wulang
 */
@Service
public class OverviewService {

    private final JobInfoDao jobDao;
    private final JobReportDao reportDao;

    @Autowired
    public OverviewService(JobInfoDao jobDao, JobReportDao reportDao) {
        this.jobDao = jobDao;
        this.reportDao = reportDao;
    }

    /**
     * 统计图表数据
     *
     * @return 统计图表数据
     */
    public JobCountVO jobCount() {
        JobReport report = reportDao.sumLogReportTotal();
        if (report != null) {
            long total = report.getRunningCount() + report.getSuccessCount() + report.getFailCount();
            return JobCountVO.of(total, report.getSuccessCount(), report.getFailCount(), report.getRunningCount());
        } else {
            return JobCountVO.of(0L, 0L, 0L, 0L);
        }
    }

    public JobDailyCountVO jobDailyCount(Date startDate, Date endDate, String sort) {
        List<String> triggerDayList = new ArrayList<>();
        List<Long> runningList = new ArrayList<>();
        List<Long> successList = new ArrayList<>();
        List<Long> failList = new ArrayList<>();

        List<JobReport> logReportList = reportDao.findByTriggerDay(startDate, endDate, sort);

        if (!CollectionUtils.isEmpty(logReportList)) {
            for (JobReport item : logReportList) {
                String day = SimpleDateUtils.format(item.getTriggerDay(), DatePattern.DATE_PATTERN);

                long running = item.getRunningCount();
                long success = item.getSuccessCount();
                long fail = item.getFailCount();

                triggerDayList.add(day);
                runningList.add(running);
                successList.add(success);
                failList.add(fail);
            }
        } else {
            for (int i = -6; i <= 0; i++) {
                Date offsetDay = SimpleDateUtils.offsetDay(new Date(), i);
                String day = SimpleDateUtils.format(offsetDay, DatePattern.DATE_PATTERN);

                triggerDayList.add(day);
                runningList.add(0L);
                successList.add(0L);
                failList.add(0L);
            }
        }

        return JobDailyCountVO.of(triggerDayList, runningList, successList, failList);
    }

    public JobStatusCountVO jobStatusCount() {
        return jobDao.jobStatusCount();
    }
}
