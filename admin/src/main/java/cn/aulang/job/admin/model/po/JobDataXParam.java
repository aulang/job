package cn.aulang.job.admin.model.po;

import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 交换任务参数
 *
 * @author wulang
 */
@Data
@Table(name = "job_datax_param")
@EqualsAndHashCode(callSuper = true)
public class JobDataXParam extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务ID
     */
    @NotNull
    private Long jobId;
    /**
     * 起始数据源
     */
    @NotNull
    private Long srcDsId;

    /**
     * 源表，和query_sql二选一
     */
    private String srcTable;
    /**
     * 源数据列，关系型数据库为字段逗号分隔，非关系型为字段名称:类型逗号分隔
     */
    private String srcColumn;
    /**
     * 过滤条件，和query_sql二选一
     */
    private String whera;
    /**
     * 查询语句
     */
    private String querySql;
    /**
     * 整型主键任务切分字段
     */
    private String splitPk;

    /**
     * 目标数据源
     */
    @NotNull
    private Long destDsId;
    /**
     * 目标表
     */
    @NotBlank
    private String destTable;
    /**
     * 目标数据列，关系型数据库为字段逗号分隔，非关系型为字段名称:类型逗号分隔
     */
    @NotBlank
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
     * JVM参数
     */
    private String jvmParam;
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
    private Long incrStartValue;
    /**
     * 时间增量格式
     */
    private String timePattern;
    /**
     * 动态替换参数
     */
    private String replaceParam;
    /**
     * 分区信息
     */
    private String partitionInfo;

    /**
     * 数据流向：0流入、1流出
     */
    private Integer flow;
    /**
     * 业务类型：0数据归集、1数据集成、2数据共享
     */
    private Integer bizType;

    /**
     * 通道数
     */
    private Integer channel = 1;
    /**
     * 每通道处理速度，2M/s
     */
    private Integer speed = 2097152;
    /**
     * 批量处理条数
     */
    private Integer batchSize = 256;
    /**
     * 允许错误百分比
     */
    private Double percentage = 0.02;
    /**
     * 允许错误记录数
     */
    private Integer record;

    /**
     * 更新时间
     */
    private Date updateTime;
}
