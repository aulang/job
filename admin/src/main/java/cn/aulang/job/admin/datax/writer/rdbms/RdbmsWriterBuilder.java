package cn.aulang.job.admin.datax.writer.rdbms;

import cn.aulang.job.core.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 关系型数据库写入器构建器
 *
 * @author wulang
 */
public class RdbmsWriterBuilder {

    private final RdbmsWriter writer = new RdbmsWriter();
    private final WriteParameter parameter = new WriteParameter();
    private final WriteConnection connection = new WriteConnection();

    /**
     * 名称
     *
     * @param name 读取器名称
     * @return 构建器
     */
    public RdbmsWriterBuilder name(String name) {
        writer.setName(name);
        return this;
    }

    /**
     * JDB连接
     *
     * @param jdbcUrl JDB连接
     * @return 构建器
     */
    public RdbmsWriterBuilder jdbcUrl(String jdbcUrl) {
        connection.setJdbcUrl(jdbcUrl);
        return this;
    }

    /**
     * 用户名
     *
     * @param username 用户名
     * @return 构建器
     */
    public RdbmsWriterBuilder username(String username) {
        parameter.setUsername(username);
        return this;
    }

    /**
     * 密码
     *
     * @param password 密码
     * @return 构建器
     */
    public RdbmsWriterBuilder password(String password) {
        parameter.setPassword(password);
        return this;
    }

    /**
     * 表名
     *
     * @param table 表名
     * @return 构建器
     */
    public RdbmsWriterBuilder table(String table) {
        if (StringUtils.isNotBlank(table)) {
            List<String> tables = new ArrayList<>();
            tables.add(table);

            connection.setTable(tables);
        }
        return this;
    }

    /**
     * 列名，逗号分隔
     *
     * @param column 列名
     * @return 构建器
     */
    public RdbmsWriterBuilder column(String column) {
        if (StringUtils.isNotBlank(column)) {
            List<String> columns = Arrays.asList(StringUtils.split(column, Constants.SPLIT_COMMA));
            parameter.setColumn(columns);
        }
        return this;
    }

    /**
     * 只有MySQL支持，写模式：insert、replace、update
     *
     * @param writeMode 写模式
     * @return 构建器
     */
    public RdbmsWriterBuilder writeMode(String writeMode) {
        parameter.setWriteMode(writeMode);
        return this;
    }

    /**
     * 批量处理数量
     *
     * @param batchSize 批量处理数量
     * @return 构建器
     */
    public RdbmsWriterBuilder batchSize(Integer batchSize) {
        parameter.setBatchSize(batchSize);
        return this;
    }

    public RdbmsWriter build() {
        parameter.setConnection(Collections.singletonList(connection));

        parameter.check();

        writer.setParameter(parameter);
        return writer;
    }
}
