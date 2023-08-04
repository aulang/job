package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 日志内容
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private long logId;
    /**
     * 开始行
     */
    private int fromLineNum;
    /**
     * 结束行
     */
    private int toLineNum;
    /**
     * 内容
     */
    private String content;
    /**
     * 是否结束
     */
    private boolean end;

    public LogResult(int fromLineNum, int toLineNum, String content, boolean end) {
        this.fromLineNum = fromLineNum;
        this.toLineNum = toLineNum;
        this.content = content;
        this.end = end;
    }
}
