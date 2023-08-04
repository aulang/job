package cn.aulang.job.admin.feign.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author wulang
 */
@Data
public class MessageResponse {

    private String msgId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private final Date sendTime = new Date();
}
