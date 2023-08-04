package cn.aulang.job.admin.dao;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.job.admin.model.po.JobReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 任务运行报告DAO
 *
 * @author wulang
 */
@Mapper
public interface JobReportDao extends MybatisRepository<JobReport, Long> {

    /**
     * 统计任务运行状况
     *
     * @return {@link JobReport}
     */
    JobReport sumLogReportTotal();

    /**
     * 获取任务运行状况列表
     *
     * @param from 开始日期
     * @param to   结束日期
     * @param sort 日期排序
     * @return 任务运行状况列表
     */
    List<JobReport> findByTriggerDay(@Param("from") Date from, @Param("to") Date to, @Param("sort") String sort);

    /**
     * 获取指定天任务运行状况
     *
     * @param triggerDay 指定日期
     * @return 任务运行状况
     */
    JobReport getByTriggerDay(@Param("triggerDay") Date triggerDay);
}
