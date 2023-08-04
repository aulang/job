package cn.aulang.job.admin.datax.writer.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB 插入/更新设置
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UpsertInfo {

    /**
     * 开启插入/更新
     */
    private Boolean isUpsert;
    /**
     * 插入更新键
     */
    private String upsertKey;
}
