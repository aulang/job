package cn.aulang.job.admin.model.po;

import cn.aulang.job.core.common.Constants;
import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 用户
 *
 * @author wulang
 */
@Data
@Table(name = "job_user")
@EqualsAndHashCode(callSuper = true)
public class JobUser extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 账号
     */
    @NotBlank
    private String username;
    /**
     * 密码
     */
    @NotBlank
    private String password;
    /**
     * 角色：0普通用户；1管理员
     */
    @NotNull
    private Integer role = 0;
    /**
     * 执行器ID列表，多个逗号分割
     */
    private String permission;

    /**
     * 获取用户的执行器列表
     *
     * @return 用户的执行器列表
     */
    public List<String> getExecutors() {
        if (StringUtils.isNotBlank(permission)) {
            return Arrays.asList(StringUtils.split(permission, Constants.SPLIT_COMMA));
        } else {
            return Collections.emptyList();
        }
    }
}
