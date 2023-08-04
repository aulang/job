package cn.aulang.job.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JobAdmin配置
 *
 * @author wulang
 */
@Data
@ConfigurationProperties(prefix = "swater.job")
public class JobProperties {

    /**
     * 执行器通讯认证令牌
     */
    private String accessToken;

    /**
     * 是否开启心跳，有服务名均衡时无需心跳，此时请设置注册地址url
     */
    private boolean beatEnabled;
    /**
     * 心跳间隔，单位秒
     */
    private int beatInterval = 30;
    /**
     * 心跳超时时间
     */
    private int deadTimeout = 90;

    /**
     * 应用名称
     */
    private String appName;

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
     * AES加密密钥
     */
    private String aesKey;
}
