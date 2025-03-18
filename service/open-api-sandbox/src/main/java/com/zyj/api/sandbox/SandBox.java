package com.zyj.api.sandbox;

import com.zyj.model.sandbox.SandboxExecutionInput;
import com.zyj.model.sandbox.SandboxExecutionOutput;

@FunctionalInterface
public interface SandBox {
    SandboxExecutionOutput execute(SandboxExecutionInput input);
}
