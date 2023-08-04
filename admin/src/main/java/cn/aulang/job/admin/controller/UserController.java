package cn.aulang.job.admin.controller;

import cn.aulang.job.admin.model.po.JobUser;
import cn.aulang.job.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.page.Pageable;

/**
 * 用户
 *
 * @author wulang
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public Pageable<JobUser> pageList(@RequestParam(required = false) String username,
                                      @RequestParam(required = false) Integer role,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @RequestParam(required = false, defaultValue = "15") int size) {
        return userService.page(username, role, page, size);
    }
}
