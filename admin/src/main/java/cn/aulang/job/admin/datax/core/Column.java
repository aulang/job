package cn.aulang.job.admin.datax.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 非关系型数据库列
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Column {

    /**
     * 字段名
     */
    private String name;
    /**
     * 字段类型
     */
    private String type;
}
