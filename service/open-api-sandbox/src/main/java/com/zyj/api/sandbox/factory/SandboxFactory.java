package com.zyj.api.sandbox.factory;

import com.zyj.api.sandbox.AbstractSandBox;
import com.zyj.model.sandbox.lang.LanguageEnum;

public interface SandboxFactory {
    AbstractSandBox getSandBox(LanguageEnum language);
}
