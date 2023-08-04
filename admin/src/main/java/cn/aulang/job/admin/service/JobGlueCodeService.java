package cn.aulang.job.admin.service;

import cn.aulang.job.admin.dao.JobGlueCodeDao;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.po.JobGlueCode;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.common.crud.CRUDService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 任务脚本代码服务
 *
 * @author wulang
 */
@Service
public class JobGlueCodeService extends CRUDService<JobGlueCode, Long> {

    private final JobGlueCodeDao glueCodeDao;

    @Autowired
    public JobGlueCodeService(JobGlueCodeDao glueCodeDao) {
        this.glueCodeDao = glueCodeDao;
    }

    @Override
    protected JobGlueCodeDao getRepository() {
        return glueCodeDao;
    }

    public JobGlueCode getByJobId(Long jobId) {
        return glueCodeDao.findByJobId(jobId);
    }

    @Override
    public void save(JobGlueCode entity) {
        GlueTypeEnum glueType = GlueTypeEnum.match(entity.getGlueType());
        if (glueType == null || !glueType.isScript()) {
            throw new JobException("glueType: " + entity.getGlueType() + " invalid");
        }

        // Shell不能有换行符
        String glueSource = entity.getGlueSource();
        if (glueType == GlueTypeEnum.SHELL && glueSource != null) {
            entity.setGlueSource(StringUtils.remove(glueSource, StringUtils.CR));
        }

        Date now = new Date();

        JobGlueCode db = getByJobId(entity.getJobId());
        if (db == null) {
            entity.setCreateTime(now);
        } else {
            entity.setId(db.getId());
            entity.setUpdateTime(now);
        }

        glueCodeDao.saveOrUpdate(entity);
    }

    public int deleteByJobId(Long jobId) {
        return glueCodeDao.deleteByJobId(jobId);
    }
}
