package cn.aulang.job.admin.service;

import cn.aulang.job.admin.dao.JobHandlerParamDao;
import cn.aulang.job.admin.model.po.JobHandlerParam;
import cn.aulang.common.crud.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 处理器参数字段服务
 *
 * @author wulang
 */
@Service
public class JobHandlerParamService extends CRUDService<JobHandlerParam, Long> {

    private final JobHandlerParamDao handlerParamDao;

    @Autowired
    public JobHandlerParamService(JobHandlerParamDao handlerParamDao) {
        this.handlerParamDao = handlerParamDao;
    }

    @Override
    protected JobHandlerParamDao getRepository() {
        return handlerParamDao;
    }

    public int deleteByAppName(String appName) {
        return handlerParamDao.deleteByAppName(appName);
    }

    public void refreshRegister(String appName, List<JobHandlerParam> entities, Date date) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (JobHandlerParam entity : entities) {
            handlerParamDao.insertDuplicate(entity);
        }

        handlerParamDao.deleteByAppNameAndUpdateTimeLt(appName, date.getTime());
    }

    public List<JobHandlerParam> findByHandlerId(Long handlerId) {
        return handlerParamDao.findByHandlerId(handlerId);
    }
}
