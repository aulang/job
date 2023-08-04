package cn.aulang.job.admin.datax.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 同步速度
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Speed {

    /**
     * 根据整型主键切分字段，切分成的通道数
     */
    private Integer channel;
    /**
     * 通道速度，byte/s
     */
    @JsonProperty("byte")
    private Integer bytes;
}
