package cn.aulang.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 处理器注册参数
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerRegisterParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 处理器列表
     */
    private List<RegisterHandler> handlers;
}
