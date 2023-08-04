package cn.aulang.job.admin.service;

import cn.aulang.job.admin.dao.JobHandlerRegistryDao;
import cn.aulang.job.admin.model.po.JobHandlerRegistry;
import cn.aulang.common.crud.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 处理器服务
 *
 * @author wulang
 */
@Service
public class JobHandlerRegistryService extends CRUDService<JobHandlerRegistry, Long> {

    private final JobHandlerRegistryDao handlerRegistryDao;

    @Autowired
    public JobHandlerRegistryService(JobHandlerRegistryDao handlerRegistryDao) {
        this.handlerRegistryDao = handlerRegistryDao;
    }

    @Override
    protected JobHandlerRegistryDao getRepository() {
        return handlerRegistryDao;
    }

    public List<JobHandlerRegistry> findByAppName(String appName) {
        return handlerRegistryDao.findByAppName(appName);
    }

    public int deleteByAppName(String appName) {
        return handlerRegistryDao.deleteByAppName(appName);
    }

    public void refreshRegister(String appName, List<JobHandlerRegistry> entities, Date date) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (JobHandlerRegistry entity : entities) {
            handlerRegistryDao.insertDuplicate(entity);
        }

        handlerRegistryDao.deleteByAppNameAndUpdateTimeLt(appName, date.getTime());
    }
}
