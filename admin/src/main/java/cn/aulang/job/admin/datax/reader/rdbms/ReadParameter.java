package cn.aulang.job.admin.datax.reader.rdbms;

import cn.aulang.job.admin.datax.core.Parameter;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 读参数
 *
 * @author wulang
 */
@Data
public class ReadParameter implements Parameter {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 读取字段
     */
    private List<String> column;
    /**
     * 切分字段，仅支持整型字段
     */
    private String splitPk;
    /**
     * 连接信息
     */
    private List<ReadConnection> connection;
    /**
     * 批量处理数量，MySQL不支持
     */
    private Integer fetchSize;
    /**
     * 数据过滤条件
     */
    private String where;

    @Override
    public void check() throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(connection)) {
            throw new IllegalArgumentException("Connection can not be empty");
        }

        ReadConnection conn = connection.get(0);

        if (conn == null || CollectionUtils.isEmpty(conn.getJdbcUrl())) {
            throw new IllegalArgumentException("JdbcUrl can not be blank");
        }

        if (CollectionUtils.isEmpty(conn.getTable()) && CollectionUtils.isEmpty(conn.getQuerySql())) {
            throw new IllegalArgumentException("Table and querySql must have one");
        }

        if (!CollectionUtils.isEmpty(conn.getTable()) && CollectionUtils.isEmpty(column)) {
            throw new IllegalArgumentException("Column can not be empty");
        }
    }
}