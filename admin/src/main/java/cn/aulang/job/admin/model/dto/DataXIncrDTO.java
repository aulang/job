package cn.aulang.job.admin.model.dto;

import lombok.Data;

/**
 * DataX增量参数
 *
 * @author wulang
 */
@Data
public class DataXIncrDTO {

    /**
     * DataX参数ID
     */
    private Long id;
    /**
     * 增量开始值
     */
    private Long incrStartValue;
    /**
     * 增量结束值
     */
    private Long incrEndValue;

}
