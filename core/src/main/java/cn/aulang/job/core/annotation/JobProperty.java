package cn.aulang.job.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Handler参数类属性
 *
 * @author wulang
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JobProperty {

    /**
     * {@link #title}的别名
     *
     * @return 属性的标题，同时设置时title优先
     */
    String value() default "";

    /**
     * {@link #value}的别名，同时设置时title优先
     *
     * @return 属性的标题
     */
    String title() default "";

    /**
     * 是否必须，默认是
     *
     * @return 是否必须
     */
    boolean required() default true;

    /**
     * 默认值，有默认值时{@link #required}为false
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 日期格式化
     *
     * @return 日期格式化
     */
    String pattern() default "";

    /**
     * 备注
     *
     * @return 备注
     */
    String remark() default "";
}
