package cn.aulang.job.admin.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 清除日志
 *
 * @author wulang
 */
@Data
public class ClearLogDTO {

    /**
     * 清除类型
     */
    @NotNull
    private Integer type;
    /**
     * 执行器ID
     */
    private Long executorId;
    /**
     * 任务ID
     */
    private Long jobId;
}
