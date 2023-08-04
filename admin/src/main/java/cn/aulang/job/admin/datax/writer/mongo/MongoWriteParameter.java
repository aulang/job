package cn.aulang.job.admin.datax.writer.mongo;

import cn.aulang.job.admin.datax.core.Column;
import cn.aulang.job.admin.datax.core.Parameter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * MongoDB 写入参数
 *
 * @author wulang
 */
@Data
public class MongoWriteParameter implements Parameter {

    /**
     * 地址
     */
    private List<String> address;
    /**
     * 数据库名，可选
     */
    private String dbName;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 集合名
     */
    private String collectionName;
    /**
     * 集合列
     */
    private List<Column> column;

    /**
     * 插入更新信息
     */
    private UpsertInfo upsertInfo;

    @Override
    public void check() throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(address)) {
            throw new IllegalArgumentException("Address can not be blank");
        }

        if (upsertInfo != null && upsertInfo.getIsUpsert() != null
                && upsertInfo.getIsUpsert() && StringUtils.isBlank(upsertInfo.getUpsertKey())) {
            throw new IllegalArgumentException("Upsert enabled upsertKey can not be blank");
        }

        if (StringUtils.isBlank(dbName)) {
            throw new IllegalArgumentException("DB name can not be blank");
        }

        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("CollectionName can not be blank");
        }

        if (CollectionUtils.isEmpty(column)) {
            throw new IllegalArgumentException("Column can not be empty");
        }
    }
}
