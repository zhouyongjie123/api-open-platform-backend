package com.zyj.api.sandbox.controller;

import com.zyj.model.ResponseResult;
import com.zyj.model.sandbox.SandboxExecutionInput;
import com.zyj.model.sandbox.SandboxExecutionOutput;
import com.zyj.model.sandbox.lang.LanguageEnum;
import com.zyj.model.sandbox.service.DockerSandboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class IndexController {
    private final DockerSandboxService dockerSandboxService;

    @PostMapping
    public ResponseResult<SandboxExecutionOutput> execWithinSandbox(@RequestPart("file") MultipartFile file) {
        Resource resource = file.getResource();
        String content;
        try {
            content = resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SandboxExecutionInput sandboxExecutionInput = new SandboxExecutionInput();
        sandboxExecutionInput.setOriginalCode(content);
        sandboxExecutionInput.setLanguage(LanguageEnum.JAVA17);
        sandboxExecutionInput.setInputList(Arrays.asList("1 2", "7 9", "54 46", "87 46"));
        return execWithinSandbox(sandboxExecutionInput);
    }

    private ResponseResult<SandboxExecutionOutput> execWithinSandbox(SandboxExecutionInput input) {
        return new ResponseResult<SandboxExecutionOutput>()
                .setCode(200)
                .setData(dockerSandboxService.execWithinSandbox(input))
                .setMessage("执行成功");
    }
}
