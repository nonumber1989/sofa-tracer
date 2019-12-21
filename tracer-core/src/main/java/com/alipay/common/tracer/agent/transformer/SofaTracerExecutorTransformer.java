package com.alipay.common.tracer.agent.transformer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SofaTracerExecutorTransformer implements JavassistTransformer {
    private static Set<String>               EXECUTOR_CLASS_NAMES                      = new HashSet<String>();
    private static final String              RUNNABLE_CLASS_NAME                       = "java.lang.Runnable";
    private static final String              CALLABLE_CLASS_NAME                       = "java.util.concurrent.Callable";
    private static final String              THREAD_POOL_EXECUTOR_CLASS_NAME           = "java.util.concurrent.ThreadPoolExecutor";
    private static final String              SCHEDULED_THREAD_POOL_EXECUTOR_CLASS_NAME = "java.util.concurrent.ScheduledThreadPoolExecutor";
    private static final String              THREAD_FACTORY_CLASS_NAME                 = "java.util.concurrent.ThreadFactory";
    private static final Map<String, String> PARAM_TYPE_NAME_TO_DECORATE_METHOD_CLASS  = new HashMap<String, String>();

    static {
        EXECUTOR_CLASS_NAMES.add(THREAD_POOL_EXECUTOR_CLASS_NAME);
        EXECUTOR_CLASS_NAMES.add(SCHEDULED_THREAD_POOL_EXECUTOR_CLASS_NAME);

        PARAM_TYPE_NAME_TO_DECORATE_METHOD_CLASS.put(RUNNABLE_CLASS_NAME,
            "com.alipay.common.tracer.core.async.SofaTracerRunnable");
        PARAM_TYPE_NAME_TO_DECORATE_METHOD_CLASS.put(CALLABLE_CLASS_NAME,
            "com.alipay.common.tracer.core.async.SofaTracerCallable");
    }

    @Override
    public void transform(ClassInfo classInfo) throws IOException, NotFoundException,
                                              CannotCompileException {
        final CtClass clazz = classInfo.getCtClass();
        if (EXECUTOR_CLASS_NAMES.contains(classInfo.getClassName())) {

            for (CtMethod method : clazz.getDeclaredMethods()) {
                decorateMethod(method);
            }
            classInfo.setModified(true);
        } else {
            if (clazz.isPrimitive() || clazz.isArray() || clazz.isInterface()
                || clazz.isAnnotation()) {
                return;
            }
            if (!clazz.subclassOf(clazz.getClassPool().get(THREAD_POOL_EXECUTOR_CLASS_NAME))) {
                return;
            }

            final boolean modified = true;
            if (modified) {
                classInfo.setModified(true);
            }
        }
    }

    private void decorateMethod(final CtMethod method) throws NotFoundException,
                                                      CannotCompileException {
        final int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
            return;
        }

        CtClass[] parameterTypes = method.getParameterTypes();
        StringBuilder insertCode = new StringBuilder();
        for (int i = 0; i < parameterTypes.length; i++) {
            final String paramTypeName = parameterTypes[i].getName();
            if (PARAM_TYPE_NAME_TO_DECORATE_METHOD_CLASS.containsKey(paramTypeName)) {
                String code = "{ System.out.println(\"-----nonumber1989-----\"); }";
                insertCode.append(code);
            }
        }
        if (insertCode.length() > 0) {
            method.insertBefore(insertCode.toString());
        }
    }
}
