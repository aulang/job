package cn.aulang.job.admin.datax.reader.mongo;

import cn.aulang.job.admin.datax.core.Reader;

/**
 * MongoDB读取器
 *
 * @author wulang
 */
public class MongoReader implements Reader {

    private MongoReadParameter parameter;

    @Override
    public String getName() {
        return "mongodbreader";
    }

    @Override
    public MongoReadParameter getParameter() {
        return parameter;
    }

    public void setParameter(MongoReadParameter parameter) {
        this.parameter = parameter;
    }
}
