package cn.aulang.job.admin.model.po;

import cn.aulang.common.crud.id.LongIdEntity;
import cn.aulang.common.crud.id.LongIdGenId;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

import java.util.Date;

/**
 * 任务日志
 *
 * @author wulang
 */
@Data
@Table(name = "job_log")
@EqualsAndHashCode(callSuper = true)
public class JobLog extends LongIdEntity {

    @Id
    @KeySql(genId = LongIdGenId.class)
    private Long id;

    /**
     * 任务ID
     */
    @NotNull
    private Long jobId;

    /**
     * 执行器ID，冗余查询字段
     */
    @NotNull
    private Long executorId;

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
    @NotNull
    private Date triggerTime;
    /**
     * 调度结果
     */
    @NotNull
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
