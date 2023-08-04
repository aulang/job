package cn.aulang.job.admin.datax.writer.rdbms;

import cn.aulang.job.admin.datax.core.Writer;
import lombok.Data;

/**
 * 关系型数据库Writer
 *
 * @author wulang
 */
@Data
public class RdbmsWriter implements Writer {

    /**
     * 名称
     */
    private String name;

    /**
     * 参数
     */
    private WriteParameter parameter;
}
