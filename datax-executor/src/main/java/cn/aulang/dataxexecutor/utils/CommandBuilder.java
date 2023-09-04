package cn.aulang.dataxexecutor.utils;

import cn.aulang.dataxexecutor.common.DataXConstant;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.model.DataXParam;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DataX命令构建器
 *
 * @author wulang
 */
public class CommandBuilder {

    public static String[] buildExeCmd(DataXParam dataXParam, String dataXPyPath, String jobJsonFile) {
        List<String> cmdArray = new ArrayList<>();

        // python
        cmdArray.add(DataXConstant.PYTHON);
        // python /datax/bin/datax.py
        cmdArray.add(dataXPyPath);

        String args = buildParams(dataXParam);
        if (StringUtils.isNotBlank(args)) {
            // python /datax/bin/datax.py -j"jvmParam" -p"-Dkey1=123 -Dkey2='a b c'"
            cmdArray.add(args);
        }

        // python /datax/bin/datax.py -j"jvmParam" -p"-Dkey1=123 -Dkey2='a b c'" /datax/job/jobJson-xxx.json
        cmdArray.add(jobJsonFile);

        return cmdArray.toArray(new String[0]);
    }

    public static String buildParams(DataXParam dataXParam) {
        StringBuilder sb = new StringBuilder();

        String jvmParam = StringUtils.trim(dataXParam.getJvmParam());

        if (StringUtils.isNotBlank(jvmParam)) {
            sb.append(DataXConstant.JVM_CM)
                    .append(DataXConstant.TRANSFORM_QUOTES)
                    .append(jvmParam)
                    .append(DataXConstant.TRANSFORM_QUOTES);
        }

        String replaceParam = StringUtils.trimToNull(dataXParam.getReplaceParam());
        if (replaceParam != null) {
            if (!sb.isEmpty()) {
                sb.append(Constants.SPACE);
            }

            sb.append(DataXConstant.PARAMS_CM)
                    .append(DataXConstant.TRANSFORM_QUOTES)
                    .append(replaceParam)
                    .append(DataXConstant.TRANSFORM_QUOTES);

        }

        return sb.toString();
    }
}
