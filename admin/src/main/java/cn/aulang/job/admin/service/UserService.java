package cn.aulang.job.admin.service;

import cn.aulang.job.admin.dao.JobUserDao;
import cn.aulang.job.admin.model.po.JobUser;
import cn.aulang.common.crud.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.page.Pageable;
import tk.mybatis.mapper.page.SimplePage;

/**
 * 用户服务
 *
 * @author wulang
 */
@Service
public class UserService extends CRUDService<JobUser, Long> {

    private final JobUserDao jobUserDao;

    @Autowired
    public UserService(JobUserDao jobUserDao) {
        this.jobUserDao = jobUserDao;
    }

    @Override
    protected JobUserDao getRepository() {
        return jobUserDao;
    }

    public Pageable<JobUser> page(String username, Integer role, int page, int size) {
        Pageable<JobUser> pageable = new SimplePage<>(page, size);
        return pageable.setList(jobUserDao.findBy(username, role, pageable));
    }
}
