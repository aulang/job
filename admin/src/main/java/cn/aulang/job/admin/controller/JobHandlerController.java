package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.model.po.JobHandlerParam;
import cn.aulang.job.admin.model.po.JobHandlerRegistry;
import cn.aulang.job.admin.service.JobHandlerParamService;
import cn.aulang.job.admin.service.JobHandlerRegistryService;
import cn.aulang.common.web.CRUDControllerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 处理器
 *
 * @author wulang
 */
@RestController
@RequestMapping("/handler")
public class JobHandlerController extends CRUDControllerSupport<JobHandlerRegistry, Long> {

    private final JobHandlerRegistryService registryService;
    private final JobHandlerParamService handlerParamService;

    @Autowired
    public JobHandlerController(JobHandlerRegistryService registryService, JobHandlerParamService handlerParamService) {
        this.registryService = registryService;
        this.handlerParamService = handlerParamService;
    }

    @Override
    protected JobHandlerRegistryService service() {
        return registryService;
    }

    @GetMapping("/{id}/param")
    public List<JobHandlerParam> getField(@PathVariable("id") Long id) {
        return handlerParamService.findByHandlerId(id);
    }
}
