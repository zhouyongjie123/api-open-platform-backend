package com.zyj.model.sandbox.lang;

import com.zyj.model.sandbox.constant.LanguageName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Getter
public enum LanguageEnum {
    JAVA8(LanguageName.JAVA, "8", "openjdk:8-alpine"),
    JAVA17(LanguageName.JAVA, "17", "openjdk:17"),
    PYTHON3(LanguageName.PYTHON, "3.9", "python:3.9");

    private String lang;

    private String version;

    private String imageName;


}
