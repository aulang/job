package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.enums.HealthCodeEnum;
import cn.aulang.job.admin.model.po.JobRegistry;
import cn.aulang.job.admin.service.JobRegistryService;
import cn.aulang.job.core.model.Response;
import cn.aulang.common.exception.NotFoundException;
import cn.aulang.common.web.CRUDControllerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册节点服务
 *
 * @author wulang
 */
@RestController
@RequestMapping("/registry")
public class JobRegistryController extends CRUDControllerSupport<JobRegistry, Long> {

    private final JobRegistryService registryService;

    @Autowired
    public JobRegistryController(JobRegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    protected JobRegistryService service() {
        return registryService;
    }

    @PostMapping("/{id}/health-check")
    public Response<Integer> healthCheck(@PathVariable Long id) {
        JobRegistry registry = registryService.get(id);

        if (registry == null) {
            throw NotFoundException.of(id);
        }

        HealthCodeEnum healthCode = registryService.healthCheck(registry);
        return Response.success(healthCode.getCode());
    }
}
