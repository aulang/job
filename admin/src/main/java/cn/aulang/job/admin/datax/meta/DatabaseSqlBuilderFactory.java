package cn.aulang.job.admin.datax.meta;

import cn.aulang.job.admin.enums.DatabaseTypeEnum;
import cn.aulang.job.admin.exception.JobException;

/**
 * 数据库SQL语句构建器工厂
 *
 * @author wulang
 */
public class DatabaseSqlBuilderFactory {

    public static DatabaseSqlBuilder getByDbType(DatabaseTypeEnum databaseType) {
        return switch (databaseType) {
            case MYSQL, MARIADB -> BaseDatabaseSqlBuilder.getInstance();
            case ORACLE -> OracleSqlBuilder.getInstance();
            case POSTGRESQL -> PostgreSQLSqlBuilder.getInstance();
            case SQLSERVER -> SqlServerSqlBuilder.getInstance();
            default -> throw new JobException("Unsupported database: " + databaseType.getName());
        };
    }
}
