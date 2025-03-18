package com.zyj.api.sandbox.strategy;

import com.github.dockerjava.api.DockerClient;
import com.zyj.api.sandbox.AbstractSandBox;

public abstract class AbstractCompiledLanguageSandBox extends AbstractSandBox {
    protected AbstractCompiledLanguageSandBox(DockerClient dockerClient, String containerInnerVolumePath, String fileName, CompileTypeStrategy compileTypeStrategy) {
        super(dockerClient, containerInnerVolumePath, fileName, compileTypeStrategy);
    }

    protected abstract static class CompileTypeStrategy implements CompilationStrategy {
    }
}
