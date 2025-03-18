package com.zyj.api.sandbox.controller;

import com.zyj.api.sandbox.AbstractSandBox;
import com.zyj.api.sandbox.factory.SandboxFactory;
import com.zyj.model.ResponseResult;
import com.zyj.model.sandbox.SandboxExecutionInput;
import com.zyj.model.sandbox.SandboxExecutionOutput;
import com.zyj.model.sandbox.lang.LanguageEnum;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RestController
@RequestMapping("/")
public class IndexController {
    @jakarta.annotation.Resource
    private SandboxFactory sandboxFactory;

    @GetMapping
    public ResponseResult<String> hello() {
        return new ResponseResult<String>()
                .setCode(200)
                .setData("你好")
                .setMessage("world");
    }

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
        AbstractSandBox sandBox = sandboxFactory.getSandBox(input.getLanguage());
        SandboxExecutionOutput output = sandBox.execute(input);
        return new ResponseResult<SandboxExecutionOutput>()
                .setCode(200)
                .setData(output)
                .setMessage("执行成功");
    }
}
