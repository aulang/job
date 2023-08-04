package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 日志读取参数
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private long logId;
    /**
     * 日志时间
     */
    private long logDateTime;
    /**
     * 开始读取行
     */
    private int fromLineNum;

    /**
     * 读取行数
     */
    private int readLineNum = 500;
}
