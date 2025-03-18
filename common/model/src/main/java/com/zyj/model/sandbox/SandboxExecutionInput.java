package com.zyj.model.sandbox;

import com.zyj.model.sandbox.lang.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SandboxExecutionInput {
    private String originalCode;

    private LanguageEnum language;

    private List<String> inputList;
}