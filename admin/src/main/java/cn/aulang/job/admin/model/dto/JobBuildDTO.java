package cn.aulang.job.admin.model.dto;

import cn.aulang.job.admin.model.po.JobInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 任务构建数据
 *
 * @author wulang
 */
@Data
public class JobBuildDTO {

    /**
     * 任务ID
     */
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
     * 责任人
     */
    private String author;
    /**
     * 报警邮件
     */
    private String alarmEmail;

    /**
     * 任务备注
     */
    private String remark;

    /**
     * 脚本代码
     */
    private String glueSource;

    /**
     * 起始数据源
     */
    private Long jobId;

    /**
     * 起始数据源
     */
    private Long srcDsId;

    /**
     * 源表，和querySql二选一
     */
    private String srcTable;
    /**
     * 源数据列，关系型数据库为字段逗号分隔，非关系型为字段名称:类型逗号分隔
     */
    private String srcColumn;
    /**
     * 过滤条件，和querySql二选一
     */
    private String whera;
    /**
     * 整型主键任务切分字段
     */
    private String splitPk;
    /**
     * 查询语句
     */
    private String querySql;

    /**
     * 目标数据源
     */
    private Long destDsId;
    /**
     * 目标表
     */
    private String destTable;
    /**
     * 目标数据列，关系型数据库为字段逗号分隔，非关系型为字段名称:类型逗号分隔
     */
    private String destColumn;

    /**
     * 写模式，MySQL才有：insert、replace、update
     */
    private String writeMode;

    /**
     * MongoDB插入更新
     */
    private Boolean isUpsert;
    /**
     * MongoDB插入更新键
     */
    private String upsertKey;

    /**
     * 增量同步方式：1主键、2时间、3分区
     */
    private Integer incrementType;
    /**
     * 增量字段名称
     */
    private String incrementKey;
    /**
     * 增量字段开始值，整型或者时间数据
     */
    private String incrStartValue;
    /**
     * 时间增量格式
     */
    private String timePattern;

    /**
     * 数据流向：0流入、1流出
     */
    private Integer flow;
    /**
     * 业务类型：0数据归集、1数据集成、2数据共享
     */
    private Integer bizType;

    /**
     * 子任务
     */
    private List<Long> childJobIds;

    public JobInfo toJobInfo() {
        JobInfo jobInfo = new JobInfo();

        jobInfo.setId(id);
        jobInfo.setName(name);
        jobInfo.setGlueType(glueType);
        jobInfo.setGroupName(groupName);
        jobInfo.setExecutorId(executorId);
        jobInfo.setAuthor(author);
        jobInfo.setAlarmEmail(alarmEmail);
        jobInfo.setScheduleType(scheduleType);
        jobInfo.setScheduleConf(scheduleConf);
        jobInfo.setRouteStrategy(routeStrategy);
        jobInfo.setBlockStrategy(blockStrategy);
        jobInfo.setMisfireStrategy(misfireStrategy);

        jobInfo.setExecutorHandler(executorHandler);
        jobInfo.setExecutorParam(executorParam);
        jobInfo.setTimeout(timeout);
        jobInfo.setFailRetry(failRetry);
        jobInfo.setRemark(remark);

        jobInfo.setChildJobIds(childJobIds);

        return jobInfo;
    }

    public DataXParamDTO toDataXParam() {
        DataXParamDTO dataXParam = new DataXParamDTO();

        dataXParam.setJobId(jobId);
        dataXParam.setSrcDsId(srcDsId);
        dataXParam.setSrcTable(srcTable);
        dataXParam.setSrcColumn(srcColumn);
        dataXParam.setWhera(whera);
        dataXParam.setQuerySql(querySql);

        dataXParam.setSplitPk(splitPk);

        dataXParam.setIncrementType(incrementType);
        dataXParam.setIncrementKey(incrementKey);
        dataXParam.setTimePattern(timePattern);
        dataXParam.setIncrStartValue(incrStartValue);

        dataXParam.setDestDsId(destDsId);
        dataXParam.setDestTable(destTable);
        dataXParam.setDestColumn(destColumn);
        dataXParam.setWriteMode(writeMode);
        dataXParam.setIsUpsert(isUpsert);
        dataXParam.setUpsertKey(upsertKey);

        dataXParam.setFlow(flow);
        dataXParam.setBizType(bizType);

        return dataXParam;
    }
}
