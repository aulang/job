package cn.aulang.job.admin.datax.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 列
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column {

    /**
     * 表名
     */
    private String table;

    /**
     * 字段名
     */
    private String name;
    /**
     * 字段类型
     */
    private String type;

    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;
    /**
     * 是否可空
     */
    private Boolean isNullable;

    /**
     * 字段注释
     */
    private String comment;
}
