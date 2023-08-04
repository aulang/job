package cn.aulang.job.admin.datax.reader.rdbms;

import cn.aulang.job.admin.datax.core.Reader;
import lombok.Data;

/**
 * 关系型数据库Reader
 *
 * @author wulang
 */
@Data
public class RdbmsReader implements Reader {

    /**
     * 名称
     */
    private String name;

    /**
     * 参数
     */
    private ReadParameter parameter;
}
