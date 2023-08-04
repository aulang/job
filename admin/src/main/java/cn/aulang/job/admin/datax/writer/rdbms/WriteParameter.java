package cn.aulang.job.admin.datax.writer.rdbms;

import cn.aulang.job.admin.datax.core.Parameter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 写参数
 *
 * @author wulang
 */
@Data
public class WriteParameter implements Parameter {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 只有MySQL支持，写模式：insert、replace、update
     */
    private String writeMode;
    /**
     * 读取字段
     */
    private List<String> column;
    /**
     * 连接信息
     */
    private List<WriteConnection> connection;

    /**
     * 连接会话设置
     */
    private List<String> session;
    /**
     * 前置执行语句
     */
    private List<String> preSql;
    /**
     * 后置执行语句
     */
    private List<String> postSql;

    /**
     * 批量处理数量
     */
    private Integer batchSize;

    @Override
    public void check() throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(connection)) {
            throw new IllegalArgumentException("Connection can not be empty");
        }

        WriteConnection conn = connection.get(0);

        if (conn == null || StringUtils.isBlank(conn.getJdbcUrl())) {
            throw new IllegalArgumentException("JdbcUrl can not be blank");
        }

        if (CollectionUtils.isEmpty(conn.getTable())) {
            throw new IllegalArgumentException("Table can not be blank");
        }

        if (CollectionUtils.isEmpty(column)) {
            throw new IllegalArgumentException("Column can not be empty");
        }
    }
}