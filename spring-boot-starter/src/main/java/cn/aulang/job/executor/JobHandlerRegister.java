package cn.aulang.job.executor;

import cn.aulang.job.core.annotation.Job;
import cn.aulang.job.core.annotation.JobProperty;
import cn.aulang.job.core.api.AdminApi;
import cn.aulang.job.core.handler.IJobHandler;
import cn.aulang.job.core.handler.ParamJobHandler;
import cn.aulang.job.core.handler.impl.MethodJobHandler;
import cn.aulang.job.core.model.HandlerParamField;
import cn.aulang.job.core.model.HandlerRegisterParam;
import cn.aulang.job.core.model.RegisterHandler;
import cn.aulang.job.core.model.Response;
import cn.aulang.job.core.utils.FieldUtils;
import cn.aulang.job.service.JobExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务处理器注册器
 *
 * @author wulang
 */
@Slf4j
public class JobHandlerRegister {

    private static final ReflectionUtils.MethodFilter JOB_METHODS = method -> AnnotatedElementUtils.hasAnnotation(method, Job.class);

    protected final ApplicationContext applicationContext;
    protected final AdminApi adminApi;
    protected final String accessToken;
    protected final String appName;

    public JobHandlerRegister(ApplicationContext applicationContext, AdminApi adminApi, String accessToken, String appName) {
        this.applicationContext = applicationContext;
        this.adminApi = adminApi;
        this.accessToken = accessToken;
        this.appName = appName;
    }

    public void register() {
        Map<String, IJobHandler> handlersMap = scanJobHandler();
        Map<String, IJobHandler> jobsMap = scanJobAnnotation();

        Set<String> result = new HashSet<>(handlersMap.keySet());

        result.retainAll(jobsMap.keySet());

        if (!result.isEmpty()) {
            // Job的名称重复
            throw new IllegalArgumentException("Duplicate job handler name: " + StringUtils.join(result));
        }

        // 要一次性注册，Admin会删除不在这里的Handler
        List<IJobHandler> jobHandlers = new ArrayList<>();
        jobHandlers.addAll(handlersMap.values());
        jobHandlers.addAll(jobsMap.values());

        // 本地注册JobHandler
        JobExecutorService.registerHandlers(jobHandlers);
        // 向Admin注册JobHandler
        registerHandler(jobHandlers);
    }

    /**
     * 扫描IJobHandler实现类
     *
     * @return IJobHandler实现类
     */
    protected Map<String, IJobHandler> scanJobHandler() {
        return applicationContext.getBeansOfType(IJobHandler.class)
                .values()
                .parallelStream()
                .collect(Collectors.toMap(IJobHandler::name, Function.identity()));
    }

    /**
     * 扫描@Job注解
     *
     * @return MethodJobHandler类
     */
    protected Map<String, IJobHandler> scanJobAnnotation() {
        return applicationContext.getBeansOfType(Object.class)
                .values()
                .parallelStream()
                .flatMap(bean -> MethodIntrospector.selectMethods(bean.getClass(), JOB_METHODS)
                        .parallelStream()
                        .map(method -> {
                            Job job = AnnotatedElementUtils.findMergedAnnotation(method, Job.class);
                            if (job == null) {
                                return null;
                            }

                            ReflectionUtils.makeAccessible(method);

                            Method before = null;
                            Method after = null;

                            Class<?> beanType = bean.getClass();
                            if (StringUtils.isNotBlank(job.before())) {
                                before = ReflectionUtils.findMethod(beanType, job.before());

                                if (before == null) {
                                    throw new IllegalArgumentException("Job handler before method not exists: [" + beanType.getName()
                                            + "#" + job.before() + "]!");
                                }

                                ReflectionUtils.makeAccessible(before);
                            }

                            if (StringUtils.isNotBlank(job.after())) {
                                after = ReflectionUtils.findMethod(beanType, job.after());

                                if (after == null) {
                                    throw new IllegalArgumentException("Job handler after method not exists: [" + beanType.getName()
                                            + "#" + job.after() + "]!");
                                }

                                ReflectionUtils.makeAccessible(after);
                            }

                            return new MethodJobHandler(job.name(), job.title(), bean, method, before, after);
                        })
                        .filter(Objects::nonNull)
                )
                .collect(Collectors.toMap(MethodJobHandler::name, Function.identity()));
    }


    /**
     * 注册处理器
     *
     * @param jobHandlers 处理器列表
     */
    protected void registerHandler(Collection<IJobHandler> jobHandlers) {
        List<RegisterHandler> handlers = jobHandlers.parallelStream().map(jobHandler -> {
            RegisterHandler registryHandler = new RegisterHandler();
            registryHandler.setName(jobHandler.name());
            registryHandler.setTitle(jobHandler.title());

            List<HandlerParamField> paramFields = null;

            if (jobHandler instanceof ParamJobHandler<?> paramJobHandler) {
                paramFields = buildHandlerParam(paramJobHandler.getParamClass());
            } else if (jobHandler instanceof MethodJobHandler methodJobHandler) {
                Method method = methodJobHandler.method();
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length == 1) {
                    Class<?> paramType = parameterTypes[0];
                    paramFields = buildHandlerParam(paramType);
                }
            }

            registryHandler.setParamFields(paramFields);

            return registryHandler;
        }).toList();

        if (handlers.isEmpty()) {
            throw new IllegalStateException("Job executor has no handler!");
        }

        HandlerRegisterParam handlerRegistryParam = new HandlerRegisterParam(appName, handlers);
        log.info("Register job handler: {} to admin server", handlerRegistryParam);

        Response<String> result = adminApi.registerHandler(handlerRegistryParam, accessToken);
        if (!result.isSuccess()) {
            throw new RuntimeException("Register handler fail: " + result.getMessage());
        }
    }

    protected List<HandlerParamField> buildHandlerParam(Class<?> paramType) {
        List<HandlerParamField> paramFields = new ArrayList<>();

        ReflectionUtils.doWithFields(paramType, field -> {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                // 忽略静态和Final变量
                return;
            }

            HandlerParamField paramField = new HandlerParamField();
            paramField.setName(field.getName());

            String title = field.getName();
            JobProperty property = AnnotatedElementUtils.findMergedAnnotation(field, JobProperty.class);
            if (property != null) {
                if (StringUtils.isNotBlank(property.value())) {
                    title = property.value();
                }

                if (StringUtils.isNotBlank(property.title())) {
                    title = property.title();
                }

                if (StringUtils.isNotBlank(property.pattern())) {
                    paramField.setPattern(property.pattern());
                }

                paramField.setRequired(property.required());

                if (StringUtils.isNotBlank(property.defaultValue())) {
                    paramField.setDefaultValue(property.defaultValue());
                    paramField.setRequired(false);
                }

                paramField.setRemark(property.remark());
            }
            paramField.setTitle(title);

            paramField.setIsArray(FieldUtils.isArray(field));
            paramField.setType(FieldUtils.getType(field).getCode());

            paramFields.add(paramField);
        });

        return paramFields;
    }
}
