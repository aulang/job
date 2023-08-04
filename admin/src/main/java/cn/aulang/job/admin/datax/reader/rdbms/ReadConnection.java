package cn.aulang.job.admin.datax.reader.rdbms;

import lombok.Data;

import java.util.List;

/**
 * 读连接
 *
 * @author wulang
 */
@Data
public class ReadConnection {

    /**
     * JDBC连接
     */
    private List<String> jdbcUrl;
    /**
     * 单表查询
     */
    private List<String> table;
    /**
     * 多表联查SQL语句
     */
    private List<String> querySql;
}
