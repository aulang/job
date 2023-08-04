package cn.aulang.job.core.model;

import cn.aulang.job.core.handler.IJobParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * DataX任务参数
 *
 * @author wulang
 */
@Data
public class DataXParam implements IJobParam {

    /**
     * 任务JSON配置
     */
    private String jobJson;

    /**
     * JVM参数，DataX命令参数: -j"jvmParam"
     */
    private String jvmParam;

    /**
     * 替换参数，DataX命令参数: "-Dkey1=123 -Dkey2='a b c'"
     */
    private String replaceParam;

    @Override
    public void check() throws IllegalArgumentException {
        if (StringUtils.isBlank(jobJson)) {
            throw new IllegalArgumentException("The jobJson must not be empty!");
        }
    }
}
