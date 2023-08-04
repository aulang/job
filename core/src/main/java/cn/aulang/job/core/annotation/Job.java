package cn.aulang.job.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 任务注解
 *
 * @author wulang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Job {

    /**
     * Job英文名称，默认取方法名
     *
     * @return Job英文名称
     */
    String name() default "";

    /**
     * Job中文标题，默认取方法名
     *
     * @return Job中文标题
     */
    String title() default "";

    /**
     * 前置执行方法名，填错会抛异常哦
     *
     * @return 前置执行方法名
     */
    String before() default "";

    /**
     * 后置执行方法名，填错会抛异常哦
     *
     * @return 后置执行方法名
     */
    String after() default "";
}
