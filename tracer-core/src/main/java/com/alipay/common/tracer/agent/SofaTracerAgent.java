package com.alipay.common.tracer.agent;

import com.alipay.common.tracer.agent.transformer.JavassistTransformer;
import com.alipay.common.tracer.agent.transformer.SofaTracerExecutorTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class SofaTracerAgent {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        final List<JavassistTransformer> transformers = new ArrayList<>();
        transformers.add(new SofaTracerExecutorTransformer());
        final ClassFileTransformer transformer = new SofaTracerTransformer(transformers);
        instrumentation.addTransformer(transformer, true);
    }
}
