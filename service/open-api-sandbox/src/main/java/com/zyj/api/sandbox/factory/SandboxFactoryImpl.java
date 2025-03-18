package com.zyj.api.sandbox.factory;

import com.zyj.api.sandbox.AbstractSandBox;
import com.zyj.api.sandbox.isolation.java.Java17Sandbox;
import com.zyj.api.sandbox.isolation.java.Java8Sandbox;
import com.zyj.api.sandbox.isolation.python.Python3Sandbox;
import com.zyj.model.sandbox.lang.LanguageEnum;
import org.springframework.stereotype.Component;

@Component
public class SandboxFactoryImpl implements SandboxFactory {
    @Override
    public AbstractSandBox getSandBox(LanguageEnum language) {
        // 根据枚举值返回对应的沙箱实例
        return switch (language) {
            case JAVA17 -> new Java17Sandbox();
            case JAVA8 -> new Java8Sandbox();
            case PYTHON3 -> new Python3Sandbox();
        };
    }
}

