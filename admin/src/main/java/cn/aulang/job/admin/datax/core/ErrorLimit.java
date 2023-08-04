package cn.aulang.job.admin.datax.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出错限制
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ErrorLimit {

    /**
     * 允许错误条数
     */
    private Integer record;
    /**
     * 允许错误百分比，0.02表示2%
     */
    private Double percentage;
}
