package cn.aulang.job.admin.service;

import cn.aulang.common.core.utils.SimpleDateUtils;
import cn.aulang.job.admin.model.po.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 邮件发生服务
 *
 * @author wulang
 */
@Slf4j
@Service
public class MailSendService {

    private static final String SUBJECT = "任务调度失败提醒";
    private static final String TEMPLATE = "<p>%s你好：</p><p>任务【%s】在【%s】进行【%s】失败，请注意排查原因！</p>";

    public void sendTriggerFail(JobInfo jobInfo) {
        sendMail(jobInfo, "调度");
    }

    public void sendExeFail(JobInfo jobInfo) {
        sendMail(jobInfo, "执行");
    }

    public void sendMail(JobInfo jobInfo, String operation) {
        if (jobInfo == null || StringUtils.isBlank(jobInfo.getAlarmEmail())) {
            return;
        }

        try {
            Date date = new Date();
            String to = jobInfo.getAlarmEmail();

            String dateStr = SimpleDateUtils.format(date);
            String content = buildMsg(jobInfo.getAuthor(), jobInfo.getName(), dateStr, operation);

            // TODO 发送邮件实现
        } catch (Exception e) {
            log.error("Fail to send job failed notification", e);
        }
    }

    private String buildMsg(String author, String name, String date, String operation) {
        if (StringUtils.isNotBlank(author)) {
            author = author + "，";
        } else {
            author = "";
        }

        return String.format(TEMPLATE, author, name, date, operation);
    }
}
