package cn.aulang.job.admin.datax.parser;

import cn.aulang.job.admin.datax.db.Column;
import cn.aulang.job.admin.datax.db.Table;
import cn.aulang.common.core.utils.DatePattern;
import tk.mybatis.mapper.page.Pageable;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 数据库结构解析器
 *
 * @author wulang
 */
public interface DatabaseStructureParser extends Closeable {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.DATETIME_PATTERN);

    /**
     * 测试连接
     *
     * @return 连接是否成功
     */
    boolean test();

    /**
     * 获取所有的表
     *
     * @return 所有的表
     */
    List<Table> getTables() throws Exception;

    /**
     * 获取所有的表名
     *
     * @return 所有的表名
     */
    List<String> getTableNames() throws Exception;

    /**
     * 获取表所有的字段（表结构）
     *
     * @param tableName 表名
     * @return 表所有的字段
     */
    List<Column> getColumns(String tableName) throws Exception;

    /**
     * <p>获取表所有的字段名，关系型数据库返回名称列表，非关系返回名称:类型列表，如下</p>
     * <p>["name1", "name2", ...] or ["name1:type1", "name2:type2", ...]</p>
     *
     * @param tableName 表名
     * @return 表所有的字段名
     */
    List<String> getColumnNames(String tableName) throws Exception;

    /**
     * 获取SQL语句所有的字段，需数据库支持SQL
     *
     * @param sql SQL语句
     * @return 表所有的字段
     */
    List<Column> getSqlColumns(String sql) throws Exception;

    /**
     * 获取SQL语句所有的字段名，需数据库支持SQL
     *
     * @param sql SQL语句
     * @return 表所有的字段名
     */
    List<String> getSqlColumnNames(String sql) throws Exception;

    /**
     * 获取列最大值
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 最大值
     */
    Object getMaxValue(String tableName, String columnName) throws Exception;

    /**
     * 查询表数据
     *
     * @param tableName 表名
     * @param where     查询条件
     * @param sort      排序
     * @param page      页码
     * @param size      页大小
     * @param columns   可选列名
     * @return 表数据
     */
    Pageable<Map<String, Object>> select(String tableName, String where, String sort, int page, int size, String... columns)
            throws Exception;

    /**
     * 插入记录
     *
     * @param tableName 表名
     * @param values    值
     * @return 更新记录数
     */
    long insert(String tableName, Map<String, Object> values) throws Exception;

    /**
     * 更新记录
     *
     * @param tableName 表名
     * @param values    值
     * @return 更新记录数
     */
    long update(String tableName, Map<String, Object> values) throws Exception;

    /**
     * 删除记录
     *
     * @param tableName 表名
     * @param where     条件
     * @return 删除记录数
     */
    long delete(String tableName, Map<String, Object> where) throws Exception;

    /**
     * 删除记录
     *
     * @param tableName 表名
     * @param id        ID
     * @return 删除记录数
     */
    long deleteById(String tableName, String id) throws Exception;

    /**
     * 格式化列值
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param value      列值
     * @return 格式化之后值
     */
    default Object format(String tableName, String columnName, Object value) {
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DATE_TIME_FORMATTER);
        }

        return value;
    }

    @Override
    default void close() {
        // do nothing
    }
}
