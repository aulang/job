package cn.aulang.job.admin.model.po;

import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 数据源
 *
 * @author wulang
 */
@Data
@Table(name = "job_datasource")
@EqualsAndHashCode(callSuper = true)
public class JobDataSource extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    @NotBlank
    private String name;
    /**
     * 类型
     */
    @NotBlank
    private String type;

    /**
     * 分组
     */
    private String groupName;

    /**
     * 数据库名
     */
    private String dbName;
    /**
     * jdbc连接
     */
    @NotBlank
    private String jdbcUrl;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * jdbc驱动类
     */
    private String driverClass;

    /**
     * zookeeper地址
     */
    private String zkAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String modifier;
    /**
     * 更新时间
     */
    private Date updateTime;
}
