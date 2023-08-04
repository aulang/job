package cn.aulang.job.admin.datax.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设置
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Setting {

    /**
     * 处理速度
     */
    private Speed speed;
    /**
     * 出错限制
     */
    private ErrorLimit errorLimit;
}
