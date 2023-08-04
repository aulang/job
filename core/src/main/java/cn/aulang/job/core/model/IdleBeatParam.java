package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 执行器Job空闲查询参数
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdleBeatParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private long jobId;
}
