package cn.aulang.job.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务统计数据
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class JobCountVO {

    /**
     * 任务执行总次数
     */
    private Long jobCount;
    /**
     * 任务执行成功次数
     */
    private Long successCount;
    /**
     * 任务执行失败次数
     */
    private Long failCount;
    /**
     * 任务运行中数量
     */
    private Long runningCount;
}
