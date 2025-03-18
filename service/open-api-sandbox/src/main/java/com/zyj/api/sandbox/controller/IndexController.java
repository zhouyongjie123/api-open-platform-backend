package com.zyj.api.sandbox.controller;

import com.zyj.model.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {
    @GetMapping
    public ResponseResult<String> hello() {
        return new ResponseResult<String>()
                .setCode(200)
                .setData("你好")
                .setMessage("world");
    }
}
