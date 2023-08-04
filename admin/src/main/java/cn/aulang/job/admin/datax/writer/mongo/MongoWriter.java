package cn.aulang.job.admin.datax.writer.mongo;

import cn.aulang.job.admin.datax.core.Parameter;
import cn.aulang.job.admin.datax.core.Writer;

/**
 * MongoDB Writer
 *
 * @author wulang
 */
public class MongoWriter implements Writer {

    private MongoWriteParameter parameter;

    @Override
    public String getName() {
        return "mongodbwriter";
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(MongoWriteParameter parameter) {
        this.parameter = parameter;
    }
}
