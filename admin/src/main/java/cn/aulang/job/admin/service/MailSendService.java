package cn.aulang.job.admin.service;

import cn.aulang.job.admin.feign.MailClient;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.common.core.utils.SimpleDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 邮件发生服务
 *
 * @author wulang
 */
@Service
public class MailSendService {
    private static final Logger logger = LoggerFactory.getLogger(MailSendService.class);

    private static final String SUBJECT = "任务调度失败提醒";
    private static final String TEMPLATE = "<p>%s你好：</p><p>任务【%s】在【%s】进行【%s】失败，请注意排查原因！</p>";

    private final MailClient mailClient;

    @Autowired
    public MailSendService(MailClient mailClient) {
        this.mailClient = mailClient;
    }

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

            String dateString = SimpleDateUtils.format(date);
            String content = buildMsg(jobInfo.getAuthor(), jobInfo.getName(), dateString, operation);
            mailClient.submit(to, SUBJECT, content);
        } catch (Exception e) {
            logger.error("Fail to send job failed notification", e);
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
