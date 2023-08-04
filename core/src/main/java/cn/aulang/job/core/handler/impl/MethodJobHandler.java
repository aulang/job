package cn.aulang.job.core.handler.impl;

import cn.aulang.job.core.context.JobHelper;
import cn.aulang.job.core.handler.IJobHandler;
import cn.aulang.job.core.handler.IJobParam;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Job注解方法处理器，@Job注解的方法会被解析成该类
 *
 * @author wulang
 */
public class MethodJobHandler implements IJobHandler {

    /**
     * 名称
     */
    private final String name;
    /**
     * 标题
     */
    private final String title;

    /**
     * 目标对象
     */
    private final Object target;
    /**
     * 执行方法
     */
    private final Method method;
    /**
     * 前置执行方法
     */
    private final Method before;
    /**
     * 后置执行方法
     */
    private final Method after;

    public MethodJobHandler(String name, String title, Object target, Method method, Method before, Method after) {
        String methodName = method.getName();

        // 为空取默认值，默认值方法名
        this.name = StringUtils.isNotBlank(name) ? name : methodName;
        this.title = StringUtils.isNotBlank(title) ? title : methodName;

        this.target = target;
        this.method = method;

        this.before = before;
        this.after = after;
    }

    @Override
    public void execute() throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();

        if (paramTypes.length == 0) {
            method.invoke(target);
        } else if (paramTypes.length == 1) {
            String jobParam = JobHelper.getJobParam();

            Class<?> paramType = paramTypes[0];
            if (paramType == String.class) {
                method.invoke(target, jobParam);
                return;
            }

            // 单参数序列化
            Object param = jobMapper.readValue(JobHelper.getJobParam(), paramType);

            if (param instanceof IJobParam p) {
                p.check();
            }

            method.invoke(target, param);
        } else {
            method.invoke(target, new Object[paramTypes.length]);
        }
    }

    @Override
    public void before() throws Exception {
        if (before != null) {
            before.invoke(target);
        }
    }

    @Override
    public void after() throws Exception {
        if (after != null) {
            after.invoke(target);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String title() {
        return title;
    }

    public Method method() {
        return method;
    }
}
