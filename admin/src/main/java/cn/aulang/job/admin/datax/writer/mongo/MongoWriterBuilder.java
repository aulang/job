package cn.aulang.job.admin.datax.writer.mongo;

import cn.aulang.job.admin.datax.uitls.MongoUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * MongoDB写入器构建器
 *
 * @author wulang
 */
public class MongoWriterBuilder {

    private final MongoWriter writer = new MongoWriter();
    private final UpsertInfo upsertInfo = new UpsertInfo();
    private final MongoWriteParameter parameter = new MongoWriteParameter();

    public MongoWriterBuilder address(String jdbcUrl) {
        if (StringUtils.isNotBlank(jdbcUrl)) {
            parameter.setAddress(MongoUtils.toIpAndPort(jdbcUrl));
        }
        return this;
    }

    public MongoWriterBuilder dbName(String dbName) {
        parameter.setDbName(dbName);
        return this;
    }

    public MongoWriterBuilder username(String username) {
        parameter.setUserName(username);
        return this;
    }

    public MongoWriterBuilder password(String password) {
        parameter.setUserPassword(password);
        return this;
    }

    public MongoWriterBuilder collection(String collectionName) {
        parameter.setCollectionName(collectionName);
        return this;
    }

    public MongoWriterBuilder column(String column) {
        if (StringUtils.isNotBlank(column)) {
            parameter.setColumn(MongoUtils.parseColumn(column));
        }
        return this;
    }

    public MongoWriterBuilder isUpsert(boolean upsert) {
        upsertInfo.setIsUpsert(upsert);
        return this;
    }

    public MongoWriterBuilder upsertKey(String upsertKey) {
        upsertInfo.setUpsertKey(upsertKey);
        return this;
    }

    public MongoWriter build() {
        parameter.setUpsertInfo(upsertInfo);

        parameter.check();

        writer.setParameter(parameter);

        return writer;
    }
}
