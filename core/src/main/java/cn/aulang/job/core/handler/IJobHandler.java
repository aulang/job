package cn.aulang.job.core.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Job处理器接口
 *
 * @author wulang
 */
public interface IJobHandler {

    /**
     * JSON序列化
     */
    JsonMapper jobMapper = JsonMapper.builder()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    /**
     * Job执行
     *
     * @throws Exception 执行异常
     */
    void execute() throws Exception;


    /**
     * 前置执行
     *
     * @throws Exception 前置执行异常
     */
    default void before() throws Exception {
    }


    /**
     * 后置执行
     *
     * @throws Exception 后置执行异常
     */
    default void after() throws Exception {
    }


    /**
     * Job名称
     *
     * @return Job名称
     */
    default String name() {
        return this.getClass().getSimpleName();
    }


    /**
     * Job标题
     *
     * @return Job标题
     */
    default String title() {
        return this.getClass().getSimpleName();
    }
}
