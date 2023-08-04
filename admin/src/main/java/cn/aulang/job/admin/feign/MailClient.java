package cn.aulang.job.admin.feign;

import cn.aulang.job.admin.feign.model.MessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 邮件客户端
 *
 * @author wulang
 */
@FeignClient(name = "communication")
public interface MailClient {

    @PostMapping("/comm/mail")
    MessageResponse submit(@RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("content") String content);

}
