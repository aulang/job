package cn.aulang.dataxexecutor.utils;

import cn.aulang.dataxexecutor.common.DataXConstant;
import cn.aulang.dataxexecutor.model.LogStatistics;
import cn.aulang.job.core.common.Constants;
import cn.aulang.job.core.context.JobHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * DataX结果解析器
 *
 * @author wulang
 */
public class DataXResultParser {

    /**
     * @param inputStream 日志输入流
     * @return DataX执行结果
     * @throws IOException 读取异常
     */
    public static LogStatistics analysis(InputStream inputStream) throws IOException {
        LogStatistics logStatistics = new LogStatistics();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(DataXConstant.TASK_START_TIME_SUFFIX)) {
                    logStatistics.setTaskStartTime(subResult(line));
                } else if (line.contains(DataXConstant.TASK_END_TIME_SUFFIX)) {
                    logStatistics.setTaskEndTime(subResult(line));
                } else if (line.contains(DataXConstant.TASK_TOTAL_TIME_SUFFIX)) {
                    logStatistics.setTaskTotalTime(subResult(line));
                } else if (line.contains(DataXConstant.TASK_AVERAGE_FLOW_SUFFIX)) {
                    logStatistics.setTaskAverageFlow(subResult(line));
                } else if (line.contains(DataXConstant.TASK_RECORD_WRITING_SPEED_SUFFIX)) {
                    logStatistics.setTaskRecordWritingSpeed(subResult(line));
                } else if (line.contains(DataXConstant.TASK_RECORD_READER_NUM_SUFFIX)) {
                    logStatistics.setTaskRecordReaderNum(Integer.parseInt(subResult(line)));
                } else if (line.contains(DataXConstant.TASK_RECORD_WRITING_NUM_SUFFIX)) {
                    logStatistics.setTaskRecordWriteFailNum(Integer.parseInt(subResult(line)));
                }
                JobHelper.log(line);
            }
        }

        return logStatistics;
    }

    private static String subResult(String line) {
        if (StringUtils.isBlank(line)) {
            return Constants.STRING_BLANK;
        }

        return StringUtils.substringAfter(line, Constants.SPLIT_COLON).trim();
    }
}
