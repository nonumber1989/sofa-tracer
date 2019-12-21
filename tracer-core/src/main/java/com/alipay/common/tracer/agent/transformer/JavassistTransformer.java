package com.alipay.common.tracer.agent.transformer;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.IOException;

public interface JavassistTransformer {
    void transform(ClassInfo classInfo) throws IOException, NotFoundException,
                                       CannotCompileException;
}
