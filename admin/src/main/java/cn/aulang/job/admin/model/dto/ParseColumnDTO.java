package cn.aulang.job.admin.model.dto;

import lombok.Data;

/**
 * 解析表字段参数
 *
 * @author wulang
 */
@Data
public class ParseColumnDTO {
    /**
     * 数据源ID
     */
    private Long id;

    /**
     * 表名
     */
    private String table;
    /**
     * SQL语句
     */
    private String sql;
}
