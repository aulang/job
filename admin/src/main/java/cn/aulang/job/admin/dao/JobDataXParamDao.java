package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobDataXParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 交换任务参数DAO
 *
 * @author wulang
 */
@Mapper
public interface JobDataXParamDao extends MybatisRepository<JobDataXParam, Long> {

    JobDataXParam getByJobId(@Param("jobId") Long jobId);

    int deleteByJobId(@Param("jobId") Long jobId);

    int updateIncrStartValue(@Param("id") Long id, @Param("beforeValue") Long beforeValue, @Param("afterValue") Long afterValue);

    int countByDsId(@Param("dsId") Long dsId);
}
