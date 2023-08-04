package cn.aulang.job.admin.service;

import cn.aulang.job.admin.config.DataXProperties;
import cn.aulang.job.admin.dao.JobDataXParamDao;
import cn.aulang.job.admin.datax.core.DataXJson;
import cn.aulang.job.admin.datax.core.ErrorLimit;
import cn.aulang.job.admin.datax.core.Reader;
import cn.aulang.job.admin.datax.core.Speed;
import cn.aulang.job.admin.datax.core.Writer;
import cn.aulang.job.admin.datax.parser.DatabaseStructureParser;
import cn.aulang.job.admin.datax.reader.mongo.MongoReader;
import cn.aulang.job.admin.datax.reader.mongo.MongoReaderBuilder;
import cn.aulang.job.admin.datax.reader.rdbms.RdbmsReader;
import cn.aulang.job.admin.datax.reader.rdbms.RdbmsReaderBuilder;
import cn.aulang.job.admin.datax.writer.mongo.MongoWriter;
import cn.aulang.job.admin.datax.writer.mongo.MongoWriterBuilder;
import cn.aulang.job.admin.datax.writer.rdbms.RdbmsWriter;
import cn.aulang.job.admin.datax.writer.rdbms.RdbmsWriterBuilder;
import cn.aulang.job.admin.enums.DatabaseTypeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.dto.DataXIncrDTO;
import cn.aulang.job.admin.model.dto.DataXParamDTO;
import cn.aulang.job.admin.model.po.JobDataSource;
import cn.aulang.job.admin.model.po.JobDataXParam;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.IncrementTypeEnum;
import cn.aulang.job.core.model.DataXParam;
import cn.aulang.common.crud.CRUDService;
import cn.aulang.common.core.tools.JsonMapper;
import cn.aulang.common.core.utils.SimpleDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 交换任务参数服务
 *
 * @author wulang
 */
@Service
@EnableConfigurationProperties(DataXProperties.class)
public class JobDataXParamService extends CRUDService<JobDataXParam, Long> {

    public static final JsonMapper JSON_MAPPER = JsonMapper.nonEmptyMapper();

    private final DataXProperties properties;
    private final JobDataXParamDao dataXParamDao;
    private final JobDataSourceService dataSourceService;

    @Autowired
    public JobDataXParamService(DataXProperties properties, JobDataXParamDao dataXParamDao, JobDataSourceService dataSourceService) {
        this.properties = properties;
        this.dataXParamDao = dataXParamDao;
        this.dataSourceService = dataSourceService;
    }

    @Override
    protected JobDataXParamDao getRepository() {
        return dataXParamDao;
    }

    public int refreshIncrStartValue(DataXIncrDTO incrDTO) {
        if (incrDTO.getId() != null && incrDTO.getIncrStartValue() != null && incrDTO.getIncrEndValue() != null) {
            return dataXParamDao.updateIncrStartValue(incrDTO.getId(), incrDTO.getIncrStartValue(), incrDTO.getIncrEndValue());
        } else {
            return 0;
        }
    }

    public int deleteByJobId(Long jobId) {
        return dataXParamDao.deleteByJobId(jobId);
    }

    public DataXParam buildDataXParam(JobInfo jobInfo, DataXIncrDTO incrDTO) throws Exception {
        JobDataXParam dataXParam = dataXParamDao.getByJobId(jobInfo.getId());
        if (dataXParam == null) {
            throw new JobException("Job DataX param not exists");
        }

        JobDataSource srcDs = dataSourceService.get(dataXParam.getSrcDsId(), true);
        if (srcDs == null) {
            throw new JobException("Job DataX src datasource not exists");
        }

        JobDataSource destDs = dataSourceService.get(dataXParam.getDestDsId(), true);
        if (destDs == null) {
            throw new JobException("Job DataX dest datasource not exists");
        }

        DataXParam param = new DataXParam();
        param.setJvmParam(dataXParam.getJvmParam());
        param.setReplaceParam(dataXParam.getReplaceParam());

        Reader reader = buildReader(srcDs, dataXParam, incrDTO);
        Writer writer = buildWriter(destDs, dataXParam);

        Speed speed = buildSpeed(dataXParam);
        ErrorLimit errorLimit = buildErrorLimit(dataXParam);

        DataXJson dataXJson = DataXJson.of(reader, writer, speed, errorLimit);

        String jobJson = JSON_MAPPER.toJson(dataXJson);

        param.setJobJson(jobJson);

        return param;
    }

    private Reader buildReader(JobDataSource dataSource, JobDataXParam dataXParam, DataXIncrDTO incrDTO) throws Exception {
        DatabaseTypeEnum databaseType = DatabaseTypeEnum.match(dataSource.getType());
        if (databaseType == null) {
            throw new JobException("Unsupported database: " + dataSource.getType());
        }

        if (databaseType == DatabaseTypeEnum.MONGODB) {
            return buildMongoReader(dataSource, dataXParam);
        } else {
            return buildRdbmsReader(dataSource, dataXParam, databaseType, incrDTO);
        }
    }

    private MongoReader buildMongoReader(JobDataSource dataSource, JobDataXParam dataXParam) {
        MongoReaderBuilder builder = new MongoReaderBuilder();

        builder.address(dataSource.getJdbcUrl());
        builder.dbName(dataSource.getDbName());
        builder.username(dataSource.getUsername());
        builder.password(dataSource.getPassword());
        builder.collection(dataXParam.getSrcTable());
        builder.column(dataXParam.getSrcColumn());

        return builder.build();
    }

    private RdbmsReader buildRdbmsReader(JobDataSource dataSource, JobDataXParam dataXParam,
                                         DatabaseTypeEnum databaseType, DataXIncrDTO incrDTO) throws Exception {
        RdbmsReaderBuilder builder = new RdbmsReaderBuilder();

        builder.name(databaseType.getReaderName());
        builder.jdbcUrl(dataSource.getJdbcUrl());
        builder.username(dataSource.getUsername());
        builder.password(dataSource.getPassword());
        builder.table(dataXParam.getSrcTable());
        builder.column(dataXParam.getSrcColumn());
        builder.where(dataXParam.getWhera());
        builder.querySql(dataXParam.getQuerySql());
        builder.splitPk(dataXParam.getSplitPk());

        if (databaseType != DatabaseTypeEnum.MYSQL) {
            builder.fetchSize(dataXParam.getBatchSize());
        }

        IncrementTypeEnum incrementType = IncrementTypeEnum.match(dataXParam.getIncrementType());

        if (incrementType == null) {
            return builder.build();
        }

        String incrementKey = dataXParam.getIncrementKey();
        Long incrStartValue = dataXParam.getIncrStartValue();

        if ((incrementType == IncrementTypeEnum.ID || incrementType == IncrementTypeEnum.TIME)) {
            if (StringUtils.isBlank(incrementKey)) {
                throw new JobException("IncrementType " + incrementType.name() + " incrementKey is blank");
            }

            if (incrStartValue == null) {
                throw new JobException("IncrementType " + incrementType.name() + " incrStartValue is null");
            }
        }

        if (incrementType == IncrementTypeEnum.ID) {
            try (DatabaseStructureParser parser = dataSourceService.getParser(dataSource, false)) {
                Object endValue = parser.getMaxValue(dataXParam.getSrcTable(), incrementKey);

                if (!(endValue instanceof Number)) {
                    throw new JobException("Database incrementKey is not number type");
                }

                long incrEndValue = ((Number) endValue).longValue();

                incrDTO.setId(dataXParam.getId());
                incrDTO.setIncrStartValue(incrStartValue);
                // 下次是从incrEndValue + 1开始同步
                incrDTO.setIncrEndValue(incrEndValue + 1);

                return builder.build(dataXParam.getIncrementKey(), incrStartValue, incrEndValue);
            }
        } else if (incrementType == IncrementTypeEnum.TIME) {
            Date startTime = new Date(dataXParam.getIncrStartValue());
            Date endTime = new Date();

            incrDTO.setId(dataXParam.getId());
            incrDTO.setIncrStartValue(incrStartValue);

            Object startValue;
            Object endValue;

            String timePattern = dataXParam.getTimePattern();
            if (timePattern == null || Constants.TIMESTAMP.equalsIgnoreCase(timePattern)) {
                startValue = startTime.getTime();
                endValue = endTime.getTime();

                // 下次是从incrEndValue + 1开始同步
                incrDTO.setIncrEndValue(endTime.getTime() + 1);
            } else if (Constants.UNIX_TIMESTAMP.equalsIgnoreCase(timePattern)) {
                startValue = startTime.getTime() / 1000;
                endValue = endTime.getTime() / 1000;

                // 下次是从incrEndValue + 1000开始同步
                incrDTO.setIncrEndValue(endTime.getTime() + 1000);
            } else {
                startValue = SimpleDateUtils.format(startTime, timePattern);
                endValue = SimpleDateUtils.format(endTime, timePattern);

                if (timePattern.endsWith("s")) {
                    // 秒
                    incrDTO.setIncrEndValue(endTime.getTime() + 1000);
                } else if (timePattern.endsWith("d")) {
                    // 天
                    incrDTO.setIncrEndValue(SimpleDateUtils.offsetDay(endTime, 1).getTime());
                }
            }
            return builder.build(dataXParam.getIncrementKey(), startValue, endValue);
        } else {
            throw new JobException("RDBMS unsupported increment type: " + incrementType.name());
        }
    }

    private Writer buildWriter(JobDataSource dataSource, JobDataXParam dataXParam) {
        DatabaseTypeEnum databaseType = DatabaseTypeEnum.match(dataSource.getType());
        if (databaseType == null) {
            throw new JobException("Unsupported database: " + dataSource.getType());
        }

        if (databaseType == DatabaseTypeEnum.MONGODB) {
            return buildMongoWriter(dataSource, dataXParam);
        } else {
            return buildRdbmsWriter(dataSource, dataXParam, databaseType);
        }
    }

    private MongoWriter buildMongoWriter(JobDataSource dataSource, JobDataXParam dataXParam) {
        MongoWriterBuilder builder = new MongoWriterBuilder();

        builder.address(dataSource.getJdbcUrl());
        builder.dbName(dataSource.getDbName());
        builder.username(dataSource.getUsername());
        builder.password(dataSource.getPassword());
        builder.collection(dataXParam.getDestTable());
        builder.column(dataXParam.getDestColumn());
        builder.isUpsert(dataXParam.getIsUpsert());
        builder.upsertKey(dataXParam.getUpsertKey());

        return builder.build();
    }

    private RdbmsWriter buildRdbmsWriter(JobDataSource dataSource, JobDataXParam dataXParam, DatabaseTypeEnum databaseType) {
        RdbmsWriterBuilder builder = new RdbmsWriterBuilder();

        builder.name(databaseType.getWriterName());
        builder.jdbcUrl(dataSource.getJdbcUrl());
        builder.username(dataSource.getUsername());
        builder.password(dataSource.getPassword());
        builder.table(dataXParam.getDestTable());
        builder.column(dataXParam.getDestColumn());
        builder.batchSize(dataXParam.getBatchSize());

        if (databaseType == DatabaseTypeEnum.MYSQL) {
            builder.writeMode(dataXParam.getWriteMode());
        }

        return builder.build();
    }

    private Speed buildSpeed(JobDataXParam dataXParam) {
        Integer channel = dataXParam.getChannel() != null ? dataXParam.getChannel() : 1;
        Integer bytes = dataXParam.getSpeed() != null ? dataXParam.getSpeed() : properties.getSpeed();

        return Speed.of(channel, bytes);
    }

    private ErrorLimit buildErrorLimit(JobDataXParam dataXParam) {
        Integer record = dataXParam.getRecord();
        Double percentage = dataXParam.getPercentage();

        return ErrorLimit.of(record, percentage);
    }

    public void save(DataXParamDTO dataXParam) {
        JobDataXParam entity = dataXParam.toEntity(properties);

        JobDataXParam db = getByJobId(entity.getJobId());
        if (db != null) {
            entity.setId(db.getId());
        }

        dataXParamDao.saveOrUpdate(entity);
    }

    public JobDataXParam getByJobId(Long id) {
        return dataXParamDao.getByJobId(id);
    }
}
