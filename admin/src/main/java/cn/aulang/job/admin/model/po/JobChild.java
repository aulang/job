package cn.aulang.job.admin.model.po;

import cn.aulang.common.crud.id.LongIdEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 子任务
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_child")
@EqualsAndHashCode(callSuper = true)
public class JobChild extends LongIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private Long childId;

    public JobChild(Long jobId, Long childId) {
        this.jobId = jobId;
        this.childId = childId;
    }
}
