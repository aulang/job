package cn.aulang.job.admin.dao;

import cn.aulang.job.admin.model.po.JobGlueCode;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务脚本代码DAO
 *
 * @author wulang
 */
@Mapper
public interface JobGlueCodeDao extends MybatisRepository<JobGlueCode, Long> {

    /**
     * 根据任务ID获取任务Glue代码
     *
     * @param jobId 任务ID
     * @return 任务Glue代码
     */
    JobGlueCode findByJobId(@Param("jobId") Long jobId);

    /**
     * 根据任务ID删除任务Glue代码
     *
     * @param jobId 任务ID
     * @return 删除记录数
     */
    int deleteByJobId(@Param("jobId") Long jobId);
}
