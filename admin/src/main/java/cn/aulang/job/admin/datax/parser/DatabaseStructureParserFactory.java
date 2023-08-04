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

        switch (databaseType) {
            case MYSQL:
            case SQLSERVER:
            case POSTGRESQL:
            case MARIADB:
            case ORACLE:
                return new BaseStructureParser(dataSource);
            case MONGODB:
                return new MongoDBStructureParser(dataSource);
            default:
                throw new JobException("Unsupported database: " + dataSource.getType());
        }
    }
}
