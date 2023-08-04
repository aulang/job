package cn.aulang.job.admin.datax.reader.rdbms;

import cn.aulang.job.admin.datax.meta.BaseDatabaseSqlBuilder;
import cn.aulang.job.core.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 关系型数据库读取器构建器
 *
 * @author wulang
 */
public class RdbmsReaderBuilder {

    private final RdbmsReader reader = new RdbmsReader();
    private final ReadParameter parameter = new ReadParameter();
    private final ReadConnection connection = new ReadConnection();

    /**
     * 名称
     *
     * @param name 名称
     * @return 构建器
     */
    public RdbmsReaderBuilder name(String name) {
        reader.setName(name);
        return this;
    }

    /**
     * JDB连接
     *
     * @param jdbcUrl JDB连接
     * @return 构建器
     */
    public RdbmsReaderBuilder jdbcUrl(String jdbcUrl) {
        if (StringUtils.isNotBlank(jdbcUrl)) {
            List<String> jdbcUrls = new ArrayList<>();
            jdbcUrls.add(jdbcUrl);

            connection.setJdbcUrl(jdbcUrls);
        }

        return this;
    }

    /**
     * 用户名
     *
     * @param username 用户名
     * @return 构建器
     */
    public RdbmsReaderBuilder username(String username) {
        parameter.setUsername(username);
        return this;
    }

    /**
     * 密码
     *
     * @param password 密码
     * @return 构建器
     */
    public RdbmsReaderBuilder password(String password) {
        parameter.setPassword(password);
        return this;
    }

    /**
     * 表名
     *
     * @param table 表名
     * @return 构建器
     */
    public RdbmsReaderBuilder table(String table) {
        if (StringUtils.isNotBlank(table)) {
            List<String> tables = new ArrayList<>();
            tables.add(table);

            connection.setTable(tables);
        }
        return this;
    }

    /**
     * 查询语句
     *
     * @param querySql 查询语句
     * @return 构建器
     */
    public RdbmsReaderBuilder querySql(String querySql) {
        if (StringUtils.isNotBlank(querySql)) {
            List<String> querySqls = new ArrayList<>();
            querySqls.add(querySql);

            connection.setQuerySql(querySqls);
        }

        return this;
    }

    /**
     * 列名，逗号分隔
     *
     * @param column 列名
     * @return 构建器
     */
    public RdbmsReaderBuilder column(String column) {
        if (StringUtils.isNotBlank(column)) {
            List<String> columns = Arrays.asList(StringUtils.split(column, Constants.SPLIT_COMMA));
            parameter.setColumn(columns);
        }
        return this;
    }

    /**
     * 切分字段，仅支持整型字段
     *
     * @param splitPk 切分字段
     * @return 构建器
     */
    public RdbmsReaderBuilder splitPk(String splitPk) {
        parameter.setSplitPk(splitPk);
        return this;
    }

    /**
     * 批量处理数量，MySQL不支持
     *
     * @param fetchSize 批量处理数量
     * @return 构建器
     */
    public RdbmsReaderBuilder fetchSize(Integer fetchSize) {
        parameter.setFetchSize(fetchSize);
        return this;
    }

    /**
     * 数据过滤条件
     *
     * @param where 数据过滤条件
     * @return 构建器
     */
    public RdbmsReaderBuilder where(String where) {
        if (StringUtils.isNotBlank(where)) {
            String whereSql = where.trim();

            if (whereSql.startsWith("where")) {
                whereSql = whereSql.substring(5).trim();
            }

            parameter.setWhere(whereSql);
        }
        return this;
    }

    /**
     * 构建读取器
     *
     * @return 读取器
     */
    public RdbmsReader build() {
        parameter.setConnection(Collections.singletonList(connection));
        parameter.check();

        reader.setParameter(parameter);
        return reader;
    }

    /**
     * 构建读取器
     *
     * @param incrementKey 增量Key
     * @param startValue   开始值
     * @param endValue     结束值
     * @return 读取器
     */
    public RdbmsReader build(String incrementKey, Object startValue, Object endValue) {
        parameter.setConnection(Collections.singletonList(connection));
        parameter.check();

        String incrementWhere;
        if (startValue instanceof Number) {
            incrementWhere = String.format("%s >= %s and %s <= %s", incrementKey, startValue, incrementKey, endValue);
        } else {
            incrementWhere = String.format("%s >= '%s' and %s <= '%s'", incrementKey, startValue, incrementKey, endValue);
        }

        BaseDatabaseSqlBuilder builder = BaseDatabaseSqlBuilder.getInstance();

        List<String> querySqls = connection.getQuerySql();
        if (!CollectionUtils.isEmpty(querySqls)) {
            String querySql = querySqls.get(0);

            querySql = builder.appendWhere(querySql, incrementWhere);

            // 有了querySql，这些就不需要了
            connection.setTable(null);
            parameter.setWhere(null);
            parameter.setColumn(null);
            parameter.setSplitPk(null);

            // 清空之前的查询语句
            querySqls.clear();
            // 设置新的查询语句
            querySqls.add(querySql);
            // 设置回去
            connection.setQuerySql(querySqls);

            reader.setParameter(parameter);
            return reader;
        }

        String where = parameter.getWhere();
        if (StringUtils.isNotBlank(where)) {
            where = where + " and " + incrementWhere;
        } else {
            where = incrementWhere;
        }

        parameter.setWhere(where);
        reader.setParameter(parameter);

        return reader;
    }
}
