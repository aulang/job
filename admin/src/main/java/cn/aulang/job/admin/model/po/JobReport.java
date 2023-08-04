package cn.aulang.job.admin.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 任务运行报告
 *
 * @author wulang
 */
@Data
@Table(name = "job_report")
@EqualsAndHashCode(callSuper = true)
public class JobReport extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date triggerDay;

    @NotNull
    private Long runningCount = 0L;
    @NotNull
    private Long successCount = 0L;
    @NotNull
    private Long failCount = 0L;

    private Date updateTime;
}
