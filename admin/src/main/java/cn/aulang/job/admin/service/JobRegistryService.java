package cn.aulang.job.admin.service;

import cn.aulang.job.admin.client.ExecutorClient;
import cn.aulang.job.admin.config.JobProperties;
import cn.aulang.job.admin.dao.JobRegistryDao;
import cn.aulang.job.admin.enums.AddressTypeEnum;
import cn.aulang.job.admin.enums.HealthCodeEnum;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobRegistry;
import cn.aulang.job.core.enums.RegisterTypeEnum;
import cn.aulang.job.core.model.Response;
import cn.aulang.common.crud.CRUDService;
import cn.aulang.common.core.utils.SimpleDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 注册节点服务
 *
 * @author wulang
 */
@Service
public class JobRegistryService extends CRUDService<JobRegistry, Long> {

    private final JobProperties properties;
    private final JobRegistryDao registryDao;

    @Autowired
    public JobRegistryService(JobProperties properties, JobRegistryDao registryDao) {
        this.properties = properties;
        this.registryDao = registryDao;
    }

    @Override
    protected JobRegistryDao getRepository() {
        return registryDao;
    }

    /**
     * 获取执行器地址
     *
     * @param appName 应用名称
     * @return 执行器地址列表
     */
    public List<String> findHealthExecutorAddress(String appName) {
        // 心跳超时时间
        int timeout = properties.getDeadTimeout();
        Date updateTime = SimpleDateUtils.offsetSecond(new Date(), -timeout);

        return registryDao.findHealthExecutorAddress(
                RegisterTypeEnum.EXECUTOR.name(),
                appName,
                AddressTypeEnum.AUTO.getCode(),
                HealthCodeEnum.ONLINE.getCode(),
                updateTime,
                AddressTypeEnum.MANUAL.getCode(),
                HealthCodeEnum.OFFLINE.getCode()
        );
    }

    public List<String> findHealthAdminAddress() {
        if (properties.isBeatEnabled()) {
            // 心跳超时时间
            int timeout = properties.getDeadTimeout();
            Date updateTime = SimpleDateUtils.offsetSecond(new Date(), -timeout);
            // 查找心跳健康的节点
            return registryDao.findHealthAdminAddress(RegisterTypeEnum.ADMIN.name(), AddressTypeEnum.AUTO.getCode(), updateTime);
        } else {
            // 查找自动注册的节点
            return registryDao.findAutoAdminAddress(RegisterTypeEnum.ADMIN.name(), AddressTypeEnum.AUTO.getCode());
        }
    }

    public boolean register(String type, String appName, String address) {
        Date date = new Date();
        JobRegistry entity = registryDao.getTypeAndAppNameAndAddress(type, appName, address);
        if (entity != null) {
            registryDao.refreshUpdateTime(entity.getId(), AddressTypeEnum.AUTO.getCode(), HealthCodeEnum.ONLINE.getCode(), date);
            return false;
        } else {
            entity = new JobRegistry();
            entity.setType(type);
            entity.setAppName(appName);
            entity.setAddress(address);
            entity.setAddressType(AddressTypeEnum.AUTO.getCode());
            entity.setHealth(HealthCodeEnum.ONLINE.getCode());
            entity.setUpdateTime(date);
            registryDao.insert(entity);
            return true;
        }
    }

    public int deleteRegistry(String type, String appName, String address) {
        return registryDao.deleteByTypeAndAppNameAndAddress(type, appName, address);
    }

    /**
     * 心跳超时和手动录入节点健康监测
     */
    public void heathCheck() {
        // 心跳超时时间
        int timeout = properties.getDeadTimeout();
        Date updateTime = SimpleDateUtils.offsetSecond(new Date(), -timeout);

        List<JobRegistry> needCheckNodes = registryDao.findNeedHeathCheckNodes(
                RegisterTypeEnum.EXECUTOR.name(),
                AddressTypeEnum.AUTO.getCode(),
                updateTime,
                AddressTypeEnum.MANUAL.getCode());

        if (CollectionUtils.isEmpty(needCheckNodes)) {
            return;
        }

        ExecutorClient executorClient = new ExecutorClient();
        for (JobRegistry registry : needCheckNodes) {
            executorClient.setAddress(registry.getAddress());

            Date date = new Date();

            Response<String> result = executorClient.beat(properties.getAccessToken());
            if (result.isSuccess()) {
                registry.setHealth(HealthCodeEnum.ONLINE.getCode());
            } else if (result.isNetError()) {
                registry.setHealth(HealthCodeEnum.OFFLINE.getCode());
            } else {
                registry.setHealth(HealthCodeEnum.UNKNOWN.getCode());
            }

            if (AddressTypeEnum.AUTO.getCode() == registry.getAddressType() && result.isNetError()) {
                // 自动注册节点离线，删除该节点
                registryDao.deleteByPrimaryKey(registry.getId());
            } else {
                registry.setUpdateTime(date);
                registryDao.update(registry);
            }
        }
    }

    public List<JobRegistry> findByTypeAndAppName(String type, String appName) {
        return registryDao.findByTypeAndAppName(type, appName);
    }

    public int deleteByTypeAndAppName(String type, String appName) {
        return registryDao.deleteByTypeAndAppName(type, appName);
    }

    public HealthCodeEnum healthCheck(JobRegistry entity) {
        if (StringUtils.isBlank(entity.getAddress())) {
            return HealthCodeEnum.UNKNOWN;
        }

        ExecutorClient client = new ExecutorClient(entity.getAddress());
        Response<String> result = client.beat(properties.getAccessToken());

        HealthCodeEnum code;
        if (result.isSuccess()) {
            code = HealthCodeEnum.ONLINE;

            entity.setHealth(code.getCode());
            entity.setUpdateTime(new Date());
            registryDao.update(entity);
        } else if (result.isNetError()) {
            code = HealthCodeEnum.OFFLINE;

            if (entity.getHealth() != null && entity.getHealth() == code.getCode()) {
                return code;
            }

            entity.setHealth(code.getCode());
            entity.setUpdateTime(new Date());
            registryDao.update(entity);
        } else {
            code = HealthCodeEnum.UNKNOWN;

            entity.setHealth(code.getCode());
            entity.setUpdateTime(new Date());
            registryDao.update(entity);
        }

        return code;
    }

    @Override
    public void save(JobRegistry entity) {
        String type = entity.getType();
        if (!RegisterTypeEnum.ADMIN.name().equalsIgnoreCase(type)
                && !RegisterTypeEnum.EXECUTOR.name().equalsIgnoreCase(type)) {
            throw new JobException("Type: " + type + " invalid");
        }
        type = type.toLowerCase();

        entity.setAddressType(AddressTypeEnum.MANUAL.getCode());
        entity.setHealth(HealthCodeEnum.UNKNOWN.getCode());
        entity.setUpdateTime(new Date());
        entity.setType(type);

        registryDao.saveOrUpdate(entity);
    }
}
