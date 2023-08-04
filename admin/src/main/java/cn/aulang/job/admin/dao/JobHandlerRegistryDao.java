package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobHandlerRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 处理器DAO
 *
 * @author wulang
 */
@Mapper
public interface JobHandlerRegistryDao extends MybatisRepository<JobHandlerRegistry, Long> {

    /**
     * 根据应用名获取处理器
     *
     * @param appName 应用名
     * @return 处理器列表
     */
    List<JobHandlerRegistry> findByAppName(@Param("appName") String appName);

    /**
     * 删除应用的处理器
     *
     * @param appName 应用名
     * @return 删除记录数
     */
    int deleteByAppName(@Param("appName") String appName);

    /**
     * 新增、重复更新
     *
     * @param entity 新增记录
     * @return 新增记录数
     */
    int insertDuplicate(@Param("entity") JobHandlerRegistry entity);


    /**
     * 删除应用更新时间小于的处理器
     *
     * @param appName    应用名称
     * @param updateTime 更新时间
     * @return 删除记录数
     */
    int deleteByAppNameAndUpdateTimeLt(@Param("appName") String appName, @Param("updateTime") Long updateTime);
}
