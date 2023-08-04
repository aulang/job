package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 取消任务参数
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KillParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private long jobId;

    /**
     * 理由
     */
    private String reason;
}
