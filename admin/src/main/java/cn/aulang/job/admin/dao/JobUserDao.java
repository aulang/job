package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.page.Pageable;

import java.util.List;

/**
 * 用户DAO
 *
 * @author wulang
 */
@Mapper
public interface JobUserDao extends MybatisRepository<JobUser, Long> {

    /**
     * 查找用户
     *
     * @param username 用户名
     * @param role     角色
     * @param pageable 可选分页
     * @return 用户列表
     */
    List<JobUser> findBy(@Param("username") String username,
                         @Param("role") Integer role,
                         Pageable<?>... pageable);
}
