package cn.aulang.job.admin.datax.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交换任务内容
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Content {

    /**
     * 读取器
     */
    private Reader reader;
    /**
     * 写入器
     */
    private Writer writer;
}
