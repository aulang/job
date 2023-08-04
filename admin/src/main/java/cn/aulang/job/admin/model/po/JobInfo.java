package cn.aulang.job.admin.model.po;

import cn.aulang.job.admin.enums.JobStatusEnum;
import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 任务信息
 *
 * @author wulang
 */
@Data
@Table(name = "job_info")
@EqualsAndHashCode(callSuper = true)
public class JobInfo extends LongIdEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 执行器ID
     */
    @NotNull
    private Long executorId;

    /**
     * 任务名称
     */
    @NotBlank
    private String name;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 责任人
     */
    private String author;
    /**
     * 报警邮件
     */
    private String alarmEmail;

    /**
     * 任务类型
     */
    @NotBlank
    private String glueType;

    /**
     * 调度类型
     */
    @NotBlank
    private String scheduleType;
    /**
     * 调度配置，值含义取决于调度类型
     */
    private String scheduleConf;

    /**
     * 调度过期策略
     */
    @NotBlank
    private String misfireStrategy;
    /**
     * 执行器路由策略
     */
    @NotBlank
    private String routeStrategy;
    /**
     * 执行阻塞策略
     */
    @NotBlank
    private String blockStrategy;

    /**
     * 执行任务处理器
     */
    private String executorHandler;
    /**
     * 执行任务参数
     */
    private String executorParam;

    /**
     * 任务执行超时时间，单位秒
     */
    private Integer timeout;
    /**
     * 失败重试次数
     */
    private Integer failRetry;

    /**
     * 任务状态：0停止，1运行
     */
    @NotNull
    private Integer status = JobStatusEnum.STOP.getCode();

    /**
     * 上次调度时间
     */
    @NotNull
    private Long triggerLastTime = 0L;
    /**
     * 下次调度时间
     */
    @NotNull
    private Long triggerNextTime = 0L;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 子任务ID
     */
    @Transient
    private List<Long> childJobIds;
    /**
     * 上次调度时间
     */
    @Transient
    private Long lastTime = 0L;
}
