package cn.aulang.job.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 任务每日统计
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class JobDailyCountVO {

    private List<String> triggerDay;
    private List<Long> running;
    private List<Long> success;
    private List<Long> fail;
}
