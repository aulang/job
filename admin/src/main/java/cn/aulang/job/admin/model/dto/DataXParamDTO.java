package cn.aulang.job.admin.model.dto;

import cn.aulang.job.admin.config.DataXProperties;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobDataXParam;
import cn.aulang.job.admin.utils.NumberUtils;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.IncrementTypeEnum;
import cn.aulang.common.core.utils.SimpleDateUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * DataX参数DTO
 *
 * @author wulang
 */
@Data
public class DataXParamDTO {
    /**
     * ID
     */
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

    public JobDataXParam toEntity(DataXProperties properties) {
        JobDataXParam entity = new JobDataXParam();

        entity.setJobId(jobId);

        entity.setSrcDsId(srcDsId);

        if (StringUtils.isNotBlank(querySql)) {
            entity.setQuerySql(querySql);
        } else {
            entity.setSrcTable(srcTable);
            entity.setSrcColumn(srcColumn);
            entity.setWhera(whera);
        }

        entity.setSplitPk(splitPk);
        if (StringUtils.isNotBlank(splitPk)) {
            entity.setChannel(properties.getSplit());
            entity.setJvmParam(properties.buildJvmParam(properties.getSplit()));
        } else {
            entity.setChannel(1);
            entity.setJvmParam(properties.buildJvmParam(1));
        }

        entity.setBatchSize(properties.getBatchSize());
        entity.setSpeed(properties.getSpeed());

        IncrementTypeEnum incrementTypeEnum = IncrementTypeEnum.match(incrementType);
        if (incrementTypeEnum != null) {
            if (StringUtils.isBlank(incrementKey)) {
                throw new JobException("incrementKey must not be blank");
            }

            entity.setIncrementType(incrementType);
            entity.setIncrementKey(incrementKey);

            long startValue = 0;

            if (incrementTypeEnum == IncrementTypeEnum.ID) {
                if (NumberUtils.isNotLong(incrStartValue)) {
                    throw new JobException("ID incrStartValue: " + incrStartValue + " invalid");
                }

                startValue = NumberUtils.parseLong(incrStartValue);
            } else if (incrementTypeEnum == IncrementTypeEnum.TIME) {
                if (StringUtils.isBlank(timePattern)) {
                    throw new JobException("timePattern must not be blank");
                }

                entity.setTimePattern(timePattern);

                if (Constants.TIMESTAMP.equalsIgnoreCase(timePattern)) {
                    if (NumberUtils.isNotLong(incrStartValue)) {
                        throw new JobException("Timestamp incrStartValue: " + incrStartValue + " invalid");
                    }

                    startValue = NumberUtils.parseLong(incrStartValue);
                } else if (Constants.UNIX_TIMESTAMP.equalsIgnoreCase(timePattern)) {
                    if (NumberUtils.isNotLong(incrStartValue)) {
                        throw new JobException("UnixTimestamp incrStartValue: " + incrStartValue + " invalid");
                    }

                    startValue = NumberUtils.parseLong(incrStartValue) * 1000;
                } else {
                    Date date;
                    try {
                        date = SimpleDateUtils.parse(incrStartValue, timePattern);
                    } catch (ParseException e) {
                        throw new JobException("TimePattern: " + timePattern + ", incrStartValue: " + incrStartValue + " invalid");
                    }
                    startValue = date.getTime();
                }
            }

            entity.setIncrStartValue(startValue);
        }

        entity.setDestDsId(destDsId);
        entity.setDestTable(destTable);
        entity.setDestColumn(destColumn);

        entity.setWriteMode(writeMode);

        entity.setIsUpsert(isUpsert);
        if (isUpsert != null && isUpsert) {
            entity.setUpsertKey(upsertKey);
        }

        entity.setFlow(flow);
        entity.setBizType(bizType);
        entity.setUpdateTime(new Date());

        return entity;
    }
}
