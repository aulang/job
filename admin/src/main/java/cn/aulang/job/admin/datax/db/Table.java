package cn.aulang.job.admin.datax.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Table {

    /**
     * 表名
     */
    private String name;
    /**
     * 备注
     */
    private String comment;

    /**
     * 记录数
     */
    private Long count;
}
