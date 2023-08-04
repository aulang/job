package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处理器参数字段
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerParamField {

    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    private String type;

    /**
     * 是否必须
     */
    private Boolean required;
    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 日期格式化
     */
    private String pattern;
    /**
     * 是否数组
     */
    private Boolean isArray;

    /**
     * 备注
     */
    private String remark;
}
