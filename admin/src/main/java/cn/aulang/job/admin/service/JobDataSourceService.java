package cn.aulang.job.admin.service;

import cn.aulang.job.admin.config.JobProperties;
import cn.aulang.job.admin.dao.JobDataSourceDao;
import cn.aulang.job.admin.dao.JobDataXParamDao;
import cn.aulang.job.admin.datax.db.Column;
import cn.aulang.job.admin.datax.db.Table;
import cn.aulang.job.admin.datax.parser.DatabaseStructureParser;
import cn.aulang.job.admin.datax.parser.DatabaseStructureParserFactory;
import cn.aulang.job.admin.enums.DatabaseTypeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobDataSource;
import cn.aulang.job.admin.utils.AesUtils;
import cn.aulang.common.crud.CRUDService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.List;
import java.util.Map;

/**
 * 数据源服务
 *
 * @author wulang
 */
@Service
public class JobDataSourceService extends CRUDService<JobDataSource, Long> {

    private static final Logger logger = LoggerFactory.getLogger(JobDataSourceService.class);

    private final JobProperties properties;
    private final JobDataSourceDao dataSourceDao;
    private final JobDataXParamDao dataXParamDao;

    @Autowired
    public JobDataSourceService(JobProperties properties, JobDataSourceDao dataSourceDao, JobDataXParamDao dataXParamDao) {
        this.properties = properties;
        this.dataSourceDao = dataSourceDao;
        this.dataXParamDao = dataXParamDao;
    }

    @Override
    protected JobDataSourceDao getRepository() {
        return dataSourceDao;
    }

    @Override
    public void save(JobDataSource entity) {
        DatabaseTypeEnum databaseType = DatabaseTypeEnum.match(entity.getType());
        if (databaseType == null) {
            throw new IllegalArgumentException("Unsupported database: " + entity.getType());
        }

        String username = entity.getUsername();
        if (StringUtils.isNotBlank(username)) {
            entity.setUsername(AesUtils.encrypt(username, properties.getAesKey()));
        }

        String password = entity.getPassword();
        if (StringUtils.isNotBlank(password)) {
            entity.setPassword(AesUtils.encrypt(password, properties.getAesKey()));
        }

        String driverClass = entity.getDriverClass();
        if (StringUtils.isBlank(driverClass)) {
            entity.setDriverClass(databaseType.getDriver());
        }

        dataSourceDao.saveOrUpdate(entity);
    }

    @Override
    protected boolean onRemove(Long id) {
        int count = dataXParamDao.countByDsId(id);
        if (count > 0) {
            throw new JobException("Datasource is in use");
        }

        return true;
    }

    public JobDataSource get(Long id, boolean decrypt) {
        JobDataSource dataSource = dataSourceDao.get(id);

        if (dataSource == null) {
            return null;
        }

        if (decrypt) {
            decrypt(dataSource);
        }

        return dataSource;
    }

    private void decrypt(JobDataSource dataSource) {
        String username = dataSource.getUsername();
        if (StringUtils.isNotBlank(username)) {
            dataSource.setUsername(AesUtils.decrypt(username, properties.getAesKey()));
        }

        String password = dataSource.getPassword();
        if (StringUtils.isNotBlank(password)) {
            dataSource.setPassword(AesUtils.decrypt(password, properties.getAesKey()));
        }
    }

    public Pageable<JobDataSource> page(String name, String type, String dbName, String groupName, int page, int size) {
        Pageable<JobDataSource> pageable = new SimplePage<>(page, size);

        List<JobDataSource> dataSources = dataSourceDao.findBy(name, type, dbName, groupName, pageable);

        // 解密
        try {
            dataSources.parallelStream().forEach(this::decrypt);
        } catch (Exception e) {
            logger.error("Fail to decrypt datasource username and password", e);
        }

        return pageable.setList(dataSources);
    }

    public List<String> groupNames() {
        return dataSourceDao.findGroupNames();
    }

    public DatabaseStructureParser getParser(JobDataSource dataSource, boolean decrypt) {
        if (decrypt) {
            decrypt(dataSource);
        }

        return DatabaseStructureParserFactory.getByDataSource(dataSource);
    }

    public boolean test(JobDataSource dataSource, boolean decrypt) {
        try (DatabaseStructureParser parser = getParser(dataSource, decrypt)) {
            return parser.test();
        }
    }

    public List<String> getTableNames(JobDataSource dataSource) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.getTableNames();
        }
    }

    public List<String> getColumnNames(JobDataSource dataSource, String tableName, String sql) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            if (StringUtils.isNotBlank(sql)) {
                return parser.getSqlColumnNames(sql);
            } else {
                return parser.getColumnNames(tableName);
            }
        }
    }

    public List<Column> getColumns(JobDataSource dataSource, String tableName) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.getColumns(tableName);
        }
    }

    public List<Table> getTables(JobDataSource dataSource) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.getTables();
        }
    }


    public Pageable<Map<String, Object>> getData(JobDataSource dataSource, String tableName, String where,
                                                 String sort, int page, int size, String... columns) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.select(tableName, where, sort, page, size, columns);
        }
    }

    public long insertData(JobDataSource dataSource, String tableName, Map<String, Object> values) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.insert(tableName, values);
        }
    }

    public long updateData(JobDataSource dataSource, String tableName, Map<String, Object> values) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.update(tableName, values);
        }
    }

    public long deleteData(JobDataSource dataSource, String tableName, Map<String, Object> values) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.delete(tableName, values);
        }
    }

    public long deleteDataById(JobDataSource dataSource, String tableName, String id) throws Exception {
        try (DatabaseStructureParser parser = getParser(dataSource, true)) {
            return parser.deleteById(tableName, id);
        }
    }
}
