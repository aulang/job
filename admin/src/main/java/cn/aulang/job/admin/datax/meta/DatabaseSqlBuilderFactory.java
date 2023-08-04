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
        switch (databaseType) {
            case MYSQL:
            case MARIADB:
                return BaseDatabaseSqlBuilder.getInstance();
            case ORACLE:
                return OracleSqlBuilder.getInstance();
            case POSTGRESQL:
                return PostgreSQLSqlBuilder.getInstance();
            case SQLSERVER:
                return SqlServerSqlBuilder.getInstance();
            default:
                throw new JobException("Unsupported database: " + databaseType.getName());
        }
    }
}
