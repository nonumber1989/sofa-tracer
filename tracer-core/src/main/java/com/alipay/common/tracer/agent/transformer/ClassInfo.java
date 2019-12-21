package com.alipay.common.tracer.agent.transformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public class ClassInfo {
    private final String      className;
    private final byte[]      classFileBuffer;
    private final ClassLoader classLoader;

    private boolean           modified = false;
    private CtClass           ctClass;

    public CtClass getCtClass() throws IOException {
        if (Objects.nonNull(ctClass)) {
            return ctClass;
        }

        final ClassPool classPool = new ClassPool(true);
        if (Objects.isNull(classLoader)) {
            classPool.appendClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
        } else {
            classPool.appendClassPath(new LoaderClassPath(classLoader));
        }

        final CtClass clazz = classPool.makeClass(new ByteArrayInputStream(classFileBuffer), false);
        clazz.defrost();
        this.ctClass = clazz;
        return clazz;
    }

    public ClassInfo(String className, byte[] classFileBuffer, ClassLoader classLoader) {
        this.className = className;
        this.classFileBuffer = classFileBuffer;
        this.classLoader = classLoader;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public String getClassName() {
        return className;
    }
}
