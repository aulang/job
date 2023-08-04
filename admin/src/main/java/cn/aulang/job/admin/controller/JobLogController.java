package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.model.dto.ClearLogDTO;
import cn.aulang.job.admin.model.po.JobLog;
import cn.aulang.job.admin.model.vo.JobLogVO;
import cn.aulang.job.admin.service.JobLogService;
import cn.aulang.job.core.model.LogResult;
import cn.aulang.job.core.model.Response;
import cn.aulang.common.web.CRUDControllerSupport;
import cn.aulang.common.core.utils.SimpleDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.page.Pageable;

import java.util.Date;

/**
 * 任务日志
 *
 * @author wulang
 */
@RestController
@RequestMapping("/log")
public class JobLogController extends CRUDControllerSupport<JobLog, Long> {

    private final JobLogService logService;

    @Autowired
    public JobLogController(JobLogService logService) {
        this.logService = logService;
    }

    @Override
    protected JobLogService service() {
        return logService;
    }

    @GetMapping("/page")
    public Pageable<JobLogVO> pageList(@RequestParam(required = false) Long executorId,
                                       @RequestParam(required = false) Long jobId,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to,
                                       @RequestParam(defaultValue = "desc") String sort,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "15") int size) {
        return logService.page(executorId, jobId, name, from, to, status, sort.toLowerCase(), page, size);
    }

    @GetMapping("/{id}/cat")
    public Response<LogResult> cat(@PathVariable("id") Long id,
                                  @RequestParam(required = false) Long triggerTime,
                                  @RequestParam(defaultValue = "0") int fromLineNum,
                                  @RequestParam(defaultValue = "500") int readLineNum,
                                  @RequestParam(required = false) String executorAddress) {
        return logService.logCat(id, triggerTime, fromLineNum, readLineNum, executorAddress);
    }

    @PostMapping("/{id}/kill")
    public Response<String> kill(@PathVariable("id") Long id, @RequestParam(required = false) String reason) {
        return logService.kill(id, reason);
    }

    @PostMapping("/clear")
    public Response<String> clearLog(@RequestBody ClearLogDTO dto) {
        Date clearBeforeTime = null;
        Integer clearBeforeNum = null;

        switch (dto.getType()) {
            // 清理一个月之前日志数据
            case 1 -> clearBeforeTime = SimpleDateUtils.offsetMonth(new Date(), -1);
            // 清理三个月之前日志数据
            case 2 -> clearBeforeTime = SimpleDateUtils.offsetMonth(new Date(), -3);
            // 清理六个月之前日志数据
            case 3 -> clearBeforeTime = SimpleDateUtils.offsetMonth(new Date(), -6);
            // 清理一年之前日志数据
            case 4 -> clearBeforeTime = SimpleDateUtils.offsetYear(new Date(), -1);
            // 清理一千条以前日志数据
            case 5 -> clearBeforeNum = 1000;
            // 清理一万条以前日志数据
            case 6 -> clearBeforeNum = 10000;
            // 清理三万条以前日志数据
            case 7 -> clearBeforeNum = 300000;
            // 清理十万条以前日志数据
            case 8 -> clearBeforeNum = 1000000;
            // 清理所有日志数据
            case 9 -> clearBeforeNum = 0;
            default -> {
                return Response.fail("Clear type invalid");
            }
        }

        if (clearBeforeTime != null) {
            logService.clearBefore(dto.getExecutorId(), dto.getJobId(), clearBeforeTime);
        }

        if (clearBeforeNum != null) {
            logService.clearBeforeNum(dto.getExecutorId(), dto.getJobId(), clearBeforeNum);
        }

        return Response.success();
    }
}
