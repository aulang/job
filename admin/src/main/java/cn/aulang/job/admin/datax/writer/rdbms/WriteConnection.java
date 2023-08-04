package cn.aulang.job.admin.datax.writer.rdbms;

import lombok.Data;

import java.util.List;

/**
 * 写入连接
 *
 * @author wulang
 */
@Data
public class WriteConnection {

    /**
     * JDBC连接
     */
    private String jdbcUrl;
    /**
     * 单表查询
     */
    private List<String> table;
}
