package cn.aulang.job.admin.service;

import cn.aulang.common.core.utils.SimpleDateUtils;
import cn.aulang.common.crud.CRUDService;
import cn.aulang.job.admin.dao.JobLogDao;
import cn.aulang.job.admin.dao.JobReportDao;
import cn.aulang.job.admin.model.po.JobReport;
import cn.aulang.job.admin.model.vo.JobDailyReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 任务执行日志服务
 *
 * @author wulang
 */
@Slf4j
@Service
public class JobReportService extends CRUDService<JobReport, Long> {

    private final JobLogDao logDao;
    private final JobReportDao reportDao;

    @Autowired
    public JobReportService(JobLogDao logDao, JobReportDao reportDao) {
        this.logDao = logDao;
        this.reportDao = reportDao;
    }

    @Override
    protected JobReportDao getRepository() {
        return reportDao;
    }

    public void refreshLogReport() {
        try {
            refreshLogReport(3);
        } catch (Exception e) {
            log.error("Refresh last 3 days job report fail", e);
        }
    }

    public void refreshLogReport(int nearDays) {
        if (nearDays <= 0) {
            nearDays = 3;
        }

        Date date = new Date();

        Date from = SimpleDateUtils.beginOfDay();
        Date to = SimpleDateUtils.endOfDay();

        for (int i = 1; i <= nearDays; ++i) {
            from = SimpleDateUtils.offsetDay(from, -1);
            to = SimpleDateUtils.offsetDay(to, -1);

            JobDailyReportVO vo = logDao.getDailyReport(from, to);

            JobReport report = new JobReport();

            report.setTriggerDay(from);
            report.setRunningCount(vo.getRunningCount());
            report.setSuccessCount(vo.getSuccessCount());
            report.setFailCount(vo.getFailCount());
            report.setUpdateTime(date);

            JobReport db = reportDao.getByTriggerDay(from);
            if (db != null) {
                report.setId(db.getId());
                reportDao.update(report);
            } else {
                reportDao.insert(report);
            }
        }
    }
}
