package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 处理器
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterHandler {

    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    private String title;

    /**
     * 参数字段
     */
    private List<HandlerParamField> paramFields;
}
