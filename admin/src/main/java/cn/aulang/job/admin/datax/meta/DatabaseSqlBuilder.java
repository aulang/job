package cn.aulang.job.admin.datax.meta;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 数据库SQL语句构建器接口
 *
 * @author wulang
 */
public interface DatabaseSqlBuilder {

    /**
     * 获取表名语句
     *
     * @param dbName 数据库名
     * @return 获取表名语句
     */
    String getTableNames(String dbName);

    /**
     * 获取表名和表注释语句
     *
     * @param dbName 数据库名
     * @return 表名和表注释语句
     */
    String getTableNameAndComment(String dbName);

    /**
     * 获取表字段语句
     *
     * @param dbName    数据库名
     * @param tableName 表名
     * @return 获取表字段语句
     */
    String getColumnNames(String dbName, String tableName);

    /**
     * 获取表字段和字段注释语句
     *
     * @param dbName    数据库名
     * @param tableName 表名
     * @return 表字段和字段注释语句
     */
    String getColumnAndComment(String dbName, String tableName);


    /**
     * 获取数据库表字段和字段注释语句
     *
     * @param dbName 数据库名
     * @return 表字段和字段注释语句
     */
    String getDBColumnAndComment(String dbName);

    /**
     * 获取列最大值语句
     *
     * @param tableName  表名
     * @param columnName 字段名
     * @return 获取列最大值语句
     */
    String getMaxValue(String tableName, String columnName);

    /**
     * 获取查表语句
     *
     * @param tableName   表名
     * @param where       查询条件
     * @param sort        排序字段，可为空
     * @param page        页码
     * @param size        页大小
     * @param columnNames 字段名
     * @return 查表语句
     */
    String getSelectTable(String tableName, @Nullable String where, @Nullable String sort, int page, int size, String... columnNames);

    /**
     * 获取count表语句
     *
     * @param tableName 表名
     * @param where     查询条件
     * @return count表语句
     */
    String getCountTable(String tableName, @Nullable String where);

    /**
     * 获取插入表记录语句
     *
     * @param tableName    表名
     * @param valueColumns 插入字段
     * @return 插入表记录语句
     */
    String getInsertRow(String tableName, List<String> valueColumns);

    /**
     * 获取更新表记录语句
     *
     * @param tableName    表名
     * @param valueColumns 更新值字段
     * @param whereColumns where字段
     * @return 更新表记录语句
     */
    String getUpdateRow(String tableName, List<String> valueColumns, List<String> whereColumns);

    /**
     * 获取删除表记录语句
     *
     * @param tableName 表名
     * @param columns   where字段
     * @return 删除表记录语句
     */
    String getDeleteRow(String tableName, List<String> columns);

    /**
     * 语句后追加查询条件
     *
     * @param sql   查询语句
     * @param where 查询条件
     * @return 查询语句
     */
    String appendWhere(String sql, String where);

    /**
     * 判断查询值是否为主键
     *
     * @param value 查询值
     * @return 是否为主键
     */
    Boolean isPrimaryKey(Object value);

    /**
     * 判断查询值是否可空
     *
     * @param value 查询值
     * @return 是否可空
     */
    Boolean isNullable(Object value);
}
