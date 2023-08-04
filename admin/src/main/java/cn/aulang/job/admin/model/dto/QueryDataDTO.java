package cn.aulang.job.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 查询数据参数
 *
 * @author wulang
 */
@Data
public class QueryDataDTO {

    /**
     * 表名
     */
    @NotBlank
    private String tableName;
    /**
     * 查询条件
     */
    private String where;
    /**
     * 排序字段
     */
    private String sort;
    /**
     * 页码
     */
    private int page = 1;
    /**
     * 页大小
     */
    private int size = 15;
    /**
     * 返回字段
     */
    private List<String> columns;
}
