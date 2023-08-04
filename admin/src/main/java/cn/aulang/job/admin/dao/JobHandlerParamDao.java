package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobHandlerParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 处理器参数字段DAO
 *
 * @author wulang
 */
@Mapper
public interface JobHandlerParamDao extends MybatisRepository<JobHandlerParam, Long> {

    /**
     * 删除应用的处理器方法字段
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
    int insertDuplicate(@Param("entity") JobHandlerParam entity);


    /**
     * 获取处理器参数字段
     *
     * @param handlerId 处理器ID
     * @return 处理器参数字段列表
     */
    List<JobHandlerParam> findByHandlerId(@Param("handlerId") Long handlerId);

    /**
     * 删除应用更新时间小于的处理器
     *
     * @param appName    应用名称
     * @param updateTime 更新时间
     * @return 删除记录数
     */
    int deleteByAppNameAndUpdateTimeLt(@Param("appName") String appName, @Param("updateTime") Long updateTime);
}
