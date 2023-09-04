package cn.aulang.dataxexecutor.config;

import cn.aulang.job.core.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DataX配置
 *
 * @author wulang
 */
@ConfigurationProperties("datax")
public class DataXProperties {

    /**
     * DataX安装目录
     */
    private String homeDir = "/datax/";
    /**
     * DataX任务配置文件目录
     */
    private String jobDir = "/datax/job/";
    /**
     * DataX启动Python文件路径
     */
    private String dataXPyPath = "/datax/bin/datax.py";

    public String getHomeDir() {
        return format(homeDir);
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public String getJobDir() {
        return format(jobDir);
    }

    public void setJobDir(String jobDir) {
        this.jobDir = jobDir;
    }

    public String getDataXPyPath() {
        return dataXPyPath;
    }

    public void setDataXPyPath(String dataXPyPath) {
        this.dataXPyPath = dataXPyPath;
    }

    private String format(String path) {
        if (StringUtils.isBlank(path)) {
            return Constants.SPLIT_DIVIDE;
        }

        if (!StringUtils.endsWith(path, Constants.SPLIT_DIVIDE)) {
            return path + Constants.SPLIT_DIVIDE;
        }

        return path;
    }
}
