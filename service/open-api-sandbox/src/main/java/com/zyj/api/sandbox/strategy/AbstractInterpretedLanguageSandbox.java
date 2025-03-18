package com.zyj.api.sandbox.strategy;

import com.github.dockerjava.api.DockerClient;
import com.zyj.api.sandbox.AbstractSandBox;

public abstract class AbstractInterpretedLanguageSandbox extends AbstractSandBox {

    protected AbstractInterpretedLanguageSandbox(DockerClient dockerClient, String containerInnerVolumePath, String fileName) {
        super(dockerClient, containerInnerVolumePath, fileName, new InterpretTypeStrategy());
    }

    static class InterpretTypeStrategy implements CompilationStrategy {
        @Override
        public void compile(String containerId) {

        }
    }
}