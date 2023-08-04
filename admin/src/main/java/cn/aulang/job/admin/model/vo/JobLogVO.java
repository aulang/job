package cn.aulang.job.admin.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 任务日志VO
 *
 * @author wulang
 */
@Data
public class JobLogVO {

    private Long id;

    /**
     * 任务ID
     */
    private Long jobId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 执行器ID，冗余查询字段
     */
    private Long executorId;

    /**
     * 执行器标题
     */
    private String executorTitle;

    /**
     * 调度过期策略
     */
    private String misfireStrategy;
    /**
     * 执行器路由策略
     */
    private String routeStrategy;
    /**
     * 执行阻塞策略
     */
    private String blockStrategy;

    /**
     * 执行器地址
     */
    private String executorAddress;
    /**
     * 执行处理器
     */
    private String executorHandler;
    /**
     * 执行参数
     */
    private String executorParam;
    /**
     * 分片参数
     */
    private String shardingParam;

    /**
     * 任务执行超时时间，单位秒
     */
    private Integer timeout;
    /**
     * 失败重试次数
     */
    private Integer failRetry;

    /**
     * 触发类型
     */
    private String triggerType;

    /**
     * 调度时间
     */
    private Date triggerTime;
    /**
     * 调度结果
     */
    private Integer triggerCode;
    /**
     * 调度日志
     */
    private String triggerMsg;

    /**
     * 执行时间
     */
    private Date handleTime;
    /**
     * 执行结果
     */
    private Integer handleCode;
    /**
     * 执行日志
     */
    private String handleMsg;

    /**
     * 告警状态
     */
    private Integer alarmStatus;
}
