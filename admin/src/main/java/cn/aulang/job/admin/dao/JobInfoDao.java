package cn.aulang.job.admin.dao;

import cn.aulang.job.admin.model.po.JobChild;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.model.vo.JobStatusCountVO;
import cn.aulang.job.admin.model.vo.JobVO;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.page.Pageable;

import java.util.List;

/**
 * 任务DAO
 *
 * @author wulang
 */
@Mapper
public interface JobInfoDao extends MybatisRepository<JobInfo, Long> {

    /**
     * 统计Job数量
     *
     * @param executorId 执行器ID
     * @param status     任务状态
     * @return Job数量
     */
    int countByExecutorIdAndStatus(@Param("executorId") Long executorId, @Param("status") Integer status);

    /**
     * 获取执行器任务
     *
     * @param executorId 执行器ID
     * @param status     任务状态
     * @return 执行器任务
     */
    List<JobInfo> findByExecutorIdAndStatus(@Param("executorId") Long executorId, @Param("status") Integer status);

    /**
     * 查找Job
     *
     * @param executorId      执行器ID
     * @param groupName       任务分组
     * @param name            任务名称
     * @param status          任务状态
     * @param executorHandler 处理器
     * @param author          责任人
     * @param pageable        可选分页
     * @return 任务列表
     */
    List<JobVO> findBy(
            @Param("executorId") Long executorId,
            @Param("name") String name,
            @Param("groupName") String groupName,
            @Param("status") Integer status,
            @Param("executorHandler") String executorHandler,
            @Param("author") String author,
            Pageable<?>... pageable
    );

    /**
     * 查找分组名称列表
     *
     * @return 查找分组名称列表
     */
    List<String> findGroupNames();

    /**
     * 根据触发状态和下次触发时间查找Job
     *
     * @param status          任务状态
     * @param triggerNextTime 下次触发时间
     * @param pageable        可选分页
     * @return 任务列表
     */
    List<JobInfo> findByStatusAndTriggerNextTimeLt(@Param("status") int status,
                                                   @Param("triggerNextTime") long triggerNextTime,
                                                   Pageable<?>... pageable);

    /**
     * 更新任务触发时间
     *
     * @param jobId              任务ID
     * @param triggerCurrentTime 任务当前次触发时间
     * @param triggerNextTime    任务下次触发时间
     * @return 更新成功记录数
     */
    int updateTriggerNextTime(@Param("jobId") Long jobId,
                              @Param("triggerCurrentTime") long triggerCurrentTime,
                              @Param("triggerNextTime") long triggerNextTime);

    /**
     * 查找任务的子任务
     *
     * @param id 任务ID
     * @return 子任务列表
     */
    List<JobInfo> findChildJobs(@Param("id") Long id);

    /**
     * 任务运行状态统计
     *
     * @return 任务运行状态
     */
    JobStatusCountVO jobStatusCount();

    /**
     * 删除子任务
     *
     * @param jobId 任务ID
     * @return 删除记录数
     */
    int deleteChildJob(@Param("jobId") Long jobId);

    /**
     * 保存子任务
     *
     * @param children 子任务
     * @return 保存记录数
     */
    int saveChildJob(@Param("list") List<JobChild> children);
}
