package com.xuan.opusnocturne.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "基础测试接口", description = "用于验证系统是否成功启动联通")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "Hello World 测试", description = "返回一段欢迎语，证明Web层通了")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, OpusNocturne! 恭喜你，项目启动并联通成功！";
    }
}
