package com.alipay.common.tracer.agent;

import com.alipay.common.tracer.agent.transformer.ClassInfo;
import com.alipay.common.tracer.agent.transformer.JavassistTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SofaTracerTransformer implements ClassFileTransformer {
    private static final byte[]              EMPTY_BYTE_ARRAY = {};

    private final List<JavassistTransformer> transformers     = new ArrayList<JavassistTransformer>();

    public SofaTracerTransformer(List<? extends JavassistTransformer> transformers) {
        this.transformers.addAll(transformers);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classFileBuffer)
                                                                                      throws IllegalClassFormatException {
        try {
            if (Objects.isNull(className)) {
                return EMPTY_BYTE_ARRAY;
            }
            final String formatClassName = formatClassName(className);
            ClassInfo classInfo = new ClassInfo(formatClassName, classFileBuffer, loader);
            for (JavassistTransformer transformer : transformers) {
                transformer.transform(classInfo);
                if (classInfo.isModified()) {
                    return classInfo.getCtClass().toBytecode();
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return EMPTY_BYTE_ARRAY;
    }

    private static String formatClassName(final String className) {
        return className.replace('/', '.');
    }

}
