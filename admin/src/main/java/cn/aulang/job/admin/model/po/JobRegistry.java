package cn.aulang.job.admin.model.po;

import cn.aulang.job.admin.enums.AddressTypeEnum;
import cn.aulang.job.admin.enums.HealthCodeEnum;
import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 节点注册表
 *
 * @author wulang
 */
@Data
@Table(name = "job_registry")
@EqualsAndHashCode(callSuper = true)
public class JobRegistry extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 注册类型：ADMIN调度器、EXECUTOR执行器
     */
    @NotBlank
    private String type;

    /**
     * 应用名称
     */
    @NotBlank
    private String appName;

    /**
     * 地址
     */
    @NotBlank
    private String address;
    /**
     * 地址类型
     */
    @NotNull
    private Integer addressType = AddressTypeEnum.AUTO.getCode();

    /**
     * 健康状态
     */
    private Integer health = HealthCodeEnum.UNKNOWN.getCode();

    /**
     * 更新时间，心跳时间
     */
    private Date updateTime;
}
