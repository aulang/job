package cn.aulang.job.admin.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 任务VO
 *
 * @author wulang
 */
@Data
public class JobVO {
    /**
     * ID
     */
    private Long id;
    /**
     * 执行器ID
     */
    private Long executorId;
    /**
     * 执行器标题
     */
    private String executorTitle;

    /**
     * 任务名称
     */
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
    private String glueType;

    /**
     * 调度类型
     */
    private String scheduleType;
    /**
     * 调度配置，值含义取决于调度类型
     */
    private String scheduleConf;

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
    private Integer status;

    /**
     * 上次调度时间
     */
    private Long triggerLastTime;
    /**
     * 下次调度时间
     */
    private Long triggerNextTime;

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
}
