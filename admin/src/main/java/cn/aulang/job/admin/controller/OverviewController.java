package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.model.vo.JobCountVO;
import cn.aulang.job.admin.model.vo.JobDailyCountVO;
import cn.aulang.job.admin.model.vo.JobStatusCountVO;
import cn.aulang.job.admin.service.OverviewService;
import cn.aulang.job.core.model.Response;
import cn.aulang.common.core.utils.SimpleDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 首页统计接口
 *
 * @author wulang
 */
@RestController
@RequestMapping("/overview")
public class OverviewController {

    private final OverviewService overviewService;

    @Autowired
    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/job-count")
    public Response<JobCountVO> jobCount() {
        return new Response<>(overviewService.jobCount());
    }

    @GetMapping("/job-daily-count")
    public Response<JobDailyCountVO> jobDailyCount(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to,
            @RequestParam(defaultValue = "desc") String sort) {
        if (to == null) {
            to = SimpleDateUtils.beginOfDay();
        }

        if (from == null) {
            from = SimpleDateUtils.offsetDay(to, -30);
        }

        return new Response<>(overviewService.jobDailyCount(from, to, sort.toLowerCase()));
    }

    @GetMapping("/job-status-count")
    public Response<JobStatusCountVO> jobStatusCount() {
        return new Response<>(overviewService.jobStatusCount());
    }
}
