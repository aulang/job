package cn.aulang.job.admin.dao;

import cn.aulang.job.admin.model.po.JobLog;
import cn.aulang.job.admin.model.vo.JobDailyReportVO;
import cn.aulang.job.admin.model.vo.JobLogVO;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.page.Pageable;

import java.util.Date;
import java.util.List;

/**
 * 任务日志DAO
 *
 * @author wulang
 */
@Mapper
public interface JobLogDao extends MybatisRepository<JobLog, Long> {

    /**
     * 删除任务日志
     *
     * @param jobId 任务ID
     */
    void deleteByJobId(@Param("jobId") Long jobId);

    /**
     * 查找任务日志
     *
     * @param executorId 执行器ID
     * @param jobId      任务ID
     * @param name       任务名称
     * @param from       开始时间
     * @param to         结束时间
     * @param status     任务状态：1成功（执行成功）；2失败（触发失败和运行失败）；3运行中（执行中）
     * @param pageable   可选分页
     * @return 任务日志列表
     */
    List<JobLogVO> findBy(
            @Param("executorId") Long executorId,
            @Param("jobId") Long jobId,
            @Param("name") String name,
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("status") Integer status,
            @Param("sort") String sort,
            Pageable<?>... pageable
    );

    /**
     * 清空指定时间之前的日志
     *
     * @param executorId  执行器ID
     * @param jobId       任务ID
     * @param triggerTime 时间
     */
    void deleteByTriggerTimeLt(@Param("executorId") Long executorId, @Param("jobId") Long jobId, @Param("triggerTime") Date triggerTime);

    /**
     * 获取清除的最大ID
     *
     * @param executorId 执行器ID
     * @param jobId      任务ID
     * @param number     清除行数
     * @return 清除的最大ID
     */
    Long findClearMaxId(@Param("executorId") Long executorId, @Param("jobId") Long jobId, @Param("number") long number);

    /**
     * 清空ID小于的日志
     *
     * @param executorId 执行器ID
     * @param jobId      任务ID
     * @param id         日志ID
     */
    void deleteByIdLt(@Param("executorId") Long executorId, @Param("jobId") Long jobId, @Param("id") Long id);

    /**
     * 查找运行中的Job
     *
     * @param jobId      任务ID
     * @param handleCode 处理代码，固定值：0运行中
     * @param pageable   可选分页
     * @return 运行中的Job
     */
    List<JobLog> findRunningJob(@Param("jobId") Long jobId, @Param("handleCode") int handleCode, Pageable<?>... pageable);

    /**
     * 查找运行中的Job
     *
     * @param address    执行器地址
     * @param handleCode 处理代码，固定值：0运行中
     * @return 运行中的Job
     */
    List<JobLog> findRunningJobByExecutorAddress(@Param("executorAddress") String address, @Param("handleCode") int handleCode);

    /**
     * 获取任务运行每日报告
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 任务运行每日报告
     */
    JobDailyReportVO getDailyReport(@Param("from") Date from, @Param("to") Date to);

    /**
     * 保存触发日志信息
     *
     * @param entity 任务日志
     * @return 修改记录数
     */
    int saveTriggerInfo(@Param("entity") JobLog entity);

    /**
     * 保存回调处理信息
     *
     * @param entity 任务日志
     * @return 修改记录数
     */
    int saveHandleInfo(@Param("entity") JobLog entity);

    /**
     * 更新任务日志处理Code
     *
     * @param id         日志ID
     * @param beforeCode 之前处理Code
     * @param afterCode  之后处理Code
     * @return 更新记录数
     */
    int updateRunningHandleCode(@Param("id") Long id, @Param("beforeCode") int beforeCode, @Param("afterCode") int afterCode);
}
