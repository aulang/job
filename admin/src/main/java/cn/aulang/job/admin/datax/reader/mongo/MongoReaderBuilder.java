package cn.aulang.job.admin.datax.reader.mongo;

import cn.aulang.job.admin.datax.uitls.MongoUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * MongoDB读取器构建器
 *
 * @author wulang
 */
public class MongoReaderBuilder {

    private final MongoReader reader = new MongoReader();
    private final MongoReadParameter parameter = new MongoReadParameter();

    public MongoReaderBuilder address(String jdbcUrl) {
        if (StringUtils.isNotBlank(jdbcUrl)) {
            parameter.setAddress(MongoUtils.toIpAndPort(jdbcUrl));
        }
        return this;
    }

    public MongoReaderBuilder dbName(String dbName) {
        parameter.setDbName(dbName);
        return this;
    }

    public MongoReaderBuilder username(String username) {
        parameter.setUserName(username);
        return this;
    }

    public MongoReaderBuilder password(String password) {
        parameter.setUserPassword(password);
        return this;
    }

    public MongoReaderBuilder collection(String collectionName) {
        parameter.setCollectionName(collectionName);
        return this;
    }

    public MongoReaderBuilder column(String column) {
        if (StringUtils.isNotBlank(column)) {
            parameter.setColumn(MongoUtils.parseColumn(column));
        }
        return this;
    }

    public MongoReader build() {
        parameter.check();

        reader.setParameter(parameter);

        return reader;
    }
}
