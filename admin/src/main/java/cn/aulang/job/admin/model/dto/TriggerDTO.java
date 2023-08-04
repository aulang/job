package cn.aulang.job.admin.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 手动触发参数
 *
 * @author wulang
 */
@Data
public class TriggerDTO {

    private Long id;
    private String executorParam;
    private List<String> addresses;
}
