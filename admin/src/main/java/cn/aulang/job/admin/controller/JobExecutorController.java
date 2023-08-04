package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.model.po.JobExecutor;
import cn.aulang.job.admin.model.po.JobHandlerRegistry;
import cn.aulang.job.admin.model.po.JobRegistry;
import cn.aulang.job.admin.service.JobExecutorService;
import cn.aulang.job.admin.service.JobHandlerRegistryService;
import cn.aulang.job.admin.service.JobRegistryService;
import cn.aulang.job.core.enums.RegisterTypeEnum;
import cn.aulang.common.exception.NotFoundException;
import cn.aulang.common.web.CRUDControllerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.page.Pageable;

import java.util.List;

/**
 * 执行器
 *
 * @author wulang
 */
@RestController
@RequestMapping("/executor")
public class JobExecutorController extends CRUDControllerSupport<JobExecutor, Long> {

    private final JobExecutorService executorService;
    private final JobRegistryService registryService;
    private final JobHandlerRegistryService handlerRegistryService;

    @Autowired
    public JobExecutorController(JobExecutorService executorService,
                                 JobRegistryService registryService,
                                 JobHandlerRegistryService handlerRegistryService) {
        this.executorService = executorService;
        this.registryService = registryService;
        this.handlerRegistryService = handlerRegistryService;
    }

    @Override
    protected JobExecutorService service() {
        return executorService;
    }

    @GetMapping("/page")
    public Pageable<JobExecutor> page(
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return executorService.page(appName, title, page, size);
    }

    @GetMapping("/list")
    public List<JobExecutor> list(
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String title) {
        return executorService.list(appName, title);
    }

    @GetMapping("/{id}/node")
    public List<JobRegistry> nodes(@PathVariable("id") Long id) {
        JobExecutor executor = executorService.get(id);

        if (executor == null) {
            throw NotFoundException.of(id);
        }

        return registryService.findByTypeAndAppName(RegisterTypeEnum.EXECUTOR.name(), executor.getAppName());
    }

    @GetMapping("/{id}/handler")
    public List<JobHandlerRegistry> handler(@PathVariable("id") Long id) {
        JobExecutor executor = executorService.get(id);

        if (executor == null) {
            throw NotFoundException.of(id);
        }

        return handlerRegistryService.findByAppName(executor.getAppName());
    }
}
