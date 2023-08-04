package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobExecutor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.page.Pageable;

import java.util.List;

/**
 * 执行器DAO
 *
 * @author wulang
 */
@Mapper
public interface JobExecutorDao extends MybatisRepository<JobExecutor, Long> {

    /**
     * 查询执行器
     *
     * @param appName  应用名
     * @param title    标题
     * @param pageable 可选分页
     * @return 执行器列表
     */
    List<JobExecutor> findBy(
            @Param("appName") String appName,
            @Param("title") String title,
            Pageable<?>... pageable
    );

    /**
     * 根据应用名称获取执行器
     *
     * @param appName 应用名
     * @return 执行器
     */
    JobExecutor getByAppName(@Param("appName") String appName);
}
