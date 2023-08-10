package cn.aulang.job.admin.controller;

import cn.aulang.common.core.utils.SimpleDateUtils;
import cn.aulang.common.exception.NotFoundException;
import cn.aulang.common.web.CRUDControllerSupport;
import cn.aulang.job.admin.exception.JobException;
import cn.aulang.job.admin.model.dto.JobBuildDTO;
import cn.aulang.job.admin.model.dto.TriggerDTO;
import cn.aulang.job.admin.model.po.JobGlueCode;
import cn.aulang.job.admin.model.po.JobInfo;
import cn.aulang.job.admin.model.vo.JobVO;
import cn.aulang.job.admin.scheduler.JobScheduleHelper;
import cn.aulang.job.admin.service.JobGlueCodeService;
import cn.aulang.job.admin.service.JobInfoService;
import cn.aulang.job.admin.service.TriggerService;
import cn.aulang.job.core.enums.GlueTypeEnum;
import cn.aulang.job.core.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.page.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 任务
 *
 * @author wulang
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class JobInfoController extends CRUDControllerSupport<JobInfo, Long> {

    private final JobInfoService jobService;
    private final TriggerService triggerService;
    private final JobGlueCodeService glueCodeService;

    @Autowired
    public JobInfoController(JobInfoService jobService, TriggerService triggerService, JobGlueCodeService glueCodeService) {
        this.jobService = jobService;
        this.triggerService = triggerService;
        this.glueCodeService = glueCodeService;
    }

    @Override
    protected JobInfoService service() {
        return jobService;
    }

    @GetMapping("/page")
    public Pageable<JobVO> page(
            @RequestParam(required = false) Long executorId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String executorHandler,
            @RequestParam(required = false) String author,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "15") int size) {
        return jobService.page(executorId, name, groupName, status, executorHandler, author, page, size);
    }

    @PostMapping("/stop")
    public Response<Integer> batchStart(@RequestBody List<Long> ids) {
        int success = jobService.stop(ids);
        return Response.success(success);
    }

    @PostMapping("/start")
    public Response<Integer> batchStop(@RequestBody List<Long> ids) {
        int success = jobService.start(ids);
        return Response.success(success);
    }

    @PostMapping("/{id}/stop")
    public Response<String> stop(@PathVariable Long id) {
        return jobService.stop(id);
    }

    @PostMapping("/{id}/start")
    public Response<String> start(@PathVariable Long id) {
        return jobService.start(id);
    }

    @PostMapping("/{id}/trigger")
    public Response<String> trigger(@PathVariable Long id, @RequestBody(required = false) TriggerDTO dto) {
        JobInfo jobInfo = jobService.get(id);

        if (jobInfo == null) {
            log.warn("Job Id: {} not exists", dto.getId());
            return Response.fail("Job Id: {} not exists");
        }

        if (dto != null) {
            dto.setId(id);
            triggerService.trigger(jobInfo, dto.getExecutorParam(), dto.getAddresses());
        } else {
            triggerService.trigger(jobInfo, null, null);
        }

        return Response.success();
    }

    @GetMapping("/trigger-time")
    public Response<List<String>> nextTriggerTime(@RequestParam String scheduleType,
                                                  @RequestParam String scheduleConf) {
        if (StringUtils.isAnyBlank(scheduleType, scheduleConf)) {
            return Response.success(Collections.emptyList());
        }

        JobInfo paramJobInfo = new JobInfo();
        paramJobInfo.setScheduleType(scheduleType);
        paramJobInfo.setScheduleConf(scheduleConf);

        Date lastTime = new Date();
        List<String> result = new ArrayList<>();
        try {
            for (int i = 0; i < 7; i++) {
                lastTime = JobScheduleHelper.next(paramJobInfo, lastTime);

                if (lastTime != null) {
                    result.add(SimpleDateUtils.format(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            String msg = "Failed to get the scheduling time";
            log.error(msg, e);
            return Response.fail(msg);
        }

        return Response.success(result);
    }

    @PostMapping("/build")
    public Response<JobInfo> build(@RequestBody JobBuildDTO buildData) {
        log.info("Build job data: {}", buildData);

        GlueTypeEnum glueType = GlueTypeEnum.match(buildData.getGlueType());

        if (glueType == null) {
            throw new JobException("glueType: " + buildData.getGlueType() + " invalid");
        }

        JobInfo jobInfo = buildData.toJobInfo();

        if (glueType == GlueTypeEnum.BEAN) {
            jobService.save(jobInfo);
        } else {
            String glueSource = buildData.getGlueSource();
            jobService.save(jobInfo, glueSource);
        }

        return Response.success(jobInfo);
    }

    @GetMapping("/group")
    public List<String> groupNames() {
        return jobService.findGroupNames();
    }

    @GetMapping("/{id}/child")
    public Response<List<JobInfo>> childJob(@PathVariable Long id) {
        List<JobInfo> childJobs = jobService.findChildJobs(id);
        return Response.success(childJobs);
    }

    @GetMapping("/{id}/code")
    public Response<JobGlueCode> code(@PathVariable("id") Long id) {
        JobGlueCode glueCode = glueCodeService.getByJobId(id);

        if (glueCode == null) {
            throw NotFoundException.of(id);
        }

        return Response.success(glueCode);
    }
}
