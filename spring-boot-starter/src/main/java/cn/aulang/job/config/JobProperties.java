package cn.aulang.job.config;

import cn.aulang.job.core.enums.GlueTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JobAdmin配置
 *
 * @author wulang
 */
@Data
@ConfigurationProperties(prefix = "al-job")
public class JobProperties {

    private boolean enabled = true;

    /**
     * 调度器应用地址
     */
    private String adminUrl;
    /**
     * 认证令牌
     */
    private String accessToken;

    /**
     * 心跳间隔，单位秒
     */
    private int beatInterval = 30;

    /**
     * 重试次数
     */
    private int retry = 3;

    /**
     * 是否启用服务第负载均衡
     */
    private boolean loadBalance = false;

    /**
     * 支持的glueType，逗号分隔
     */
    private String glueTypes = GlueTypeEnum.BEAN.getName();

    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用标题
     */
    private String appTitle;

    /**
     * 注册地址，和ip、port二选一
     */
    private String url;

    /**
     * 注册IP
     */
    private String ip;
    /**
     * 注册端口
     */
    private Integer port;

    /**
     * 日志文件路径
     */
    private String logPath;
    /**
     * 日志保存天数
     */
    private Integer logRetentionDays = 30;
}
