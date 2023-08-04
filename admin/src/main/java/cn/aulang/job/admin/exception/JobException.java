package cn.aulang.job.admin.exception;

import java.io.Serial;

/**
 * 任务异常
 *
 * @author wulang
 */
public class JobException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JobException() {
    }

    public JobException(String message) {
        super(message);
    }
}
