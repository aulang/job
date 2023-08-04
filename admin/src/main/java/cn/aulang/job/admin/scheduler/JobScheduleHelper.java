package cn.aulang.job.admin.scheduler;

import cn.aulang.job.admin.cron.CronExpression;
import cn.aulang.job.admin.enums.ScheduleTypeEnum;
import cn.aulang.job.admin.model.po.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * 调度帮助类
 *
 * @author wulang
 */
public class JobScheduleHelper {

    public static final long PRE_READ_MILLISECONDS = 10000;

    private static final Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);

    public static Date next(JobInfo jobInfo, Date fromTime) {
        if (fromTime == null) {
            fromTime = new Date();
        }

        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (ScheduleTypeEnum.CRON == scheduleTypeEnum) {
            try {
                return new CronExpression(jobInfo.getScheduleConf()).getNextValidTimeAfter(fromTime);
            } catch (ParseException e) {
                logger.error("Get next scheduling time fail, corn: " + jobInfo.getScheduleConf(), e);
                return null;
            }
        } else if (ScheduleTypeEnum.FIX_RATE == scheduleTypeEnum) {
            return new Date(fromTime.getTime() + Integer.parseInt(jobInfo.getScheduleConf()) * 1000L);
        }

        return null;
    }

    public static Long nextTime(JobInfo jobInfo, Date fromTime) {
        try {
            Date nextTime = next(jobInfo, fromTime);

            if (nextTime != null) {
                return nextTime.getTime();
            }
        } catch (Exception e) {
            logger.error("Get next trigger time fail", e);
        }

        return null;
    }
}
