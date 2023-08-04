package cn.aulang.job.admin.model.po;

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
 * 任务脚本代码
 *
 * @author wulang
 */
@Data
@Table(name = "job_glue_code")
@EqualsAndHashCode(callSuper = true)
public class JobGlueCode extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long jobId;

    @NotBlank
    private String glueType;
    private String glueSource;

    private String remark;
    private Date createTime;
    private Date updateTime;
}
