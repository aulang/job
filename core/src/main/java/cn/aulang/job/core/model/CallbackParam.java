package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 执行器回调参数
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallbackParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private long jobId;
    /**
     * 日志ID
     */
    private long logId;
    /**
     * 执行结果代码
     */
    private int handleCode;
    /**
     * 执行消息，多次追加
     */
    private String handleMsg;
}
