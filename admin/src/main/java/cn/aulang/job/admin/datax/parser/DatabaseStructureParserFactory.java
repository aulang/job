package cn.aulang.job.admin.datax.parser;

import cn.aulang.job.admin.enums.DatabaseTypeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobDataSource;

/**
 * @author wulang
 */
public class DatabaseStructureParserFactory {

    public static DatabaseStructureParser getByDataSource(JobDataSource dataSource) {
        DatabaseTypeEnum databaseType = DatabaseTypeEnum.match(dataSource.getType());

        if (databaseType == null) {
            throw new JobException("Unsupported database: " + dataSource.getType());
        }

        return switch (databaseType) {
            case MYSQL, SQLSERVER, POSTGRESQL, MARIADB, ORACLE -> new BaseStructureParser(dataSource);
            case MONGODB -> new MongoDBStructureParser(dataSource);
        };
    }
}
