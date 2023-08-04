package cn.aulang.job.core.model;

import cn.aulang.job.core.enums.RegisterTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册参数
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 注册类型
     */
    private String type = RegisterTypeEnum.EXECUTOR.name();
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用标题
     */
    private String appTitle;
    /**
     * 注册地址
     */
    private String address;

    /**
     * 支持的GlueType
     */
    private String glueTypes;
}
