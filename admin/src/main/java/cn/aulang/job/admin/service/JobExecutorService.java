package cn.aulang.job.admin.service;

import cn.aulang.job.admin.dao.JobExecutorDao;
import cn.aulang.job.admin.dao.JobInfoDao;
import cn.aulang.job.admin.enums.AddressTypeEnum;
import cn.aulang.job.admin.enums.HealthCodeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobExecutor;
import cn.aulang.job.admin.model.po.JobRegistry;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.enums.RegisterTypeEnum;
import cn.aulang.common.crud.CRUDService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 执行器服务
 *
 * @author wulang
 */
@Service
public class JobExecutorService extends CRUDService<JobExecutor, Long> {

    private final JobInfoDao jobDao;
    private final JobExecutorDao executorDao;
    private final JobRegistryService registryService;

    @Autowired
    public JobExecutorService(JobInfoDao jobDao, JobExecutorDao executorDao, JobRegistryService registryService) {
        this.jobDao = jobDao;
        this.executorDao = executorDao;
        this.registryService = registryService;
    }

    @Override
    protected JobExecutorDao getRepository() {
        return executorDao;
    }

    public Pageable<JobExecutor> page(String appName, String title, int page, int size) {
        Pageable<JobExecutor> pageable = new SimplePage<>(page, size);
        return pageable.setList(executorDao.findBy(appName, title, pageable));
    }

    public List<JobExecutor> list(String appName, String title) {
        return executorDao.findBy(appName, title);
    }

    public JobExecutor getByAppName(String appName) {
        return executorDao.getByAppName(appName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(JobExecutor entity) {
        if (!entity.isNew()) {
            entity.setUpdateTime(new Date());
        }

        entity.setGlueType(StringUtils.join(entity.getGlueTypes(), Constants.SPLIT_COMMA));

        executorDao.saveOrUpdate(entity);

        String addresses = entity.getAddresses();
        if (StringUtils.isNotBlank(addresses)) {
            String[] array = StringUtils.split(addresses, Constants.SPLIT_COMMA);
            for (String address : array) {
                saveManualAddress(entity.getAppName(), address);
            }
        }
    }

    public void saveManualAddress(String appName, String address) {
        JobRegistry registry = new JobRegistry();

        registry.setType(RegisterTypeEnum.EXECUTOR.name());
        registry.setAppName(appName);
        registry.setAddress(address);
        registry.setAddressType(AddressTypeEnum.MANUAL.getCode());
        registry.setHealth(HealthCodeEnum.UNKNOWN.getCode());
        registry.setUpdateTime(new Date());

        registryService.save(registry);
    }

    public void register(String appName, String appTitle, String glueTypes) {
        JobExecutor executor = getByAppName(appName);
        if (executor != null) {
            executor.setUpdateTime(new Date());
            executor.setGlueType(checkGlueType(glueTypes));
            return;
        }

        executor = new JobExecutor();

        executor.setAppName(appName);
        executor.setGlueType(checkGlueType(glueTypes));
        executor.setTitle(StringUtils.isNotBlank(appTitle) ? appTitle : appName);
        executor.setAddressType(AddressTypeEnum.AUTO.getCode());
        executor.setUpdateTime(new Date());
        executorDao.insert(executor);
    }

    private String checkGlueType(String glueTypes) {
        List<String> list = Arrays.stream(StringUtils.split(glueTypes, Constants.SPLIT_COMMA))
                .filter(StringUtils::isNotBlank)
                .toList();

        return StringUtils.join(list, Constants.SPLIT_COMMA);
    }

    @Override
    protected boolean onRemove(Long id) {
        JobExecutor executor = executorDao.get(id);

        if (executor == null) {
            return false;
        }

        int jobCount = jobDao.countByExecutorIdAndStatus(id, null);

        if (jobCount > 0) {
            throw new JobException("Refuse to delete, the executor is in use");
        }

        registryService.deleteByTypeAndAppName(RegisterTypeEnum.EXECUTOR.name(), executor.getAppName());

        return true;
    }
}
