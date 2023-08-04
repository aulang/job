package cn.aulang.job.admin.datax.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据交换任务
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Job {

    /**
     * 设置
     */
    private Setting setting;
    /**
     * 交换任务内容
     */
    private List<Content> content;
}
