package cn.aulang.job.admin.model.vo;

import lombok.Data;

/**
 * 任务运行数量
 *
 * @author wulang
 */
@Data
public class JobStatusCountVO {

    private Integer running = 0;
    private Integer stop = 0;
}
