package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.model.po.JobGlueCode;
import cn.aulang.job.admin.service.JobGlueCodeService;
import cn.aulang.common.web.CRUDControllerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务脚本代码
 *
 * @author wulang
 */
@RestController
@RequestMapping("/code")
public class JobGlueCodeController extends CRUDControllerSupport<JobGlueCode, Long> {

    private final JobGlueCodeService glueCodeService;

    @Autowired
    public JobGlueCodeController(JobGlueCodeService glueCodeService) {
        this.glueCodeService = glueCodeService;
    }

    @Override
    protected JobGlueCodeService service() {
        return glueCodeService;
    }
}
