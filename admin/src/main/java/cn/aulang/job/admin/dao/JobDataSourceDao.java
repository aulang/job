package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.page.Pageable;

import java.util.List;

/**
 * 数据源DAO
 *
 * @author wulang
 */
@Mapper
public interface JobDataSourceDao extends MybatisRepository<JobDataSource, Long> {

    /**
     * 查找数据源
     *
     * @param name      数据源名称
     * @param type      数据库类型
     * @param dbName    数据库名
     * @param groupName 分组
     * @param pageable  可选分页
     * @return 数据源列表
     */
    List<JobDataSource> findBy(@Param("name") String name,
                               @Param("type") String type,
                               @Param("dbName") String dbName,
                               @Param("groupName") String groupName,
                               Pageable<?>... pageable);


    List<String> findGroupNames();
}
