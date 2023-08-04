package cn.aulang.job.admin.datax.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

/**
 * DataX JSON配置
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataXJson {

    /**
     * 核心全局设置配置
     */
    private Core core;

    /**
     * DataX任务配置
     */
    private Job job;

    public static DataXJson of(Reader reader, Writer writer, Speed speed, ErrorLimit errorLimit) {
        // 通道数
        int channel = speed.getChannel() != null ? speed.getChannel() : 1;
        // 单通道速度
        int bytes = speed.getBytes() != null ? speed.getBytes() : 1048576;

        // 总速度
        Speed totalSpeed = Speed.of(channel, channel * bytes);
        // 单通道速度
        Speed channelSpeed = Speed.of(null, bytes);

        Content content = Content.of(reader, writer);
        Setting setting = Setting.of(totalSpeed, errorLimit);

        Core core = Core.of(Transport.of(Channel.of(channelSpeed)));
        Job job = Job.of(setting, Collections.singletonList(content));

        return new DataXJson(core, job);
    }
}
