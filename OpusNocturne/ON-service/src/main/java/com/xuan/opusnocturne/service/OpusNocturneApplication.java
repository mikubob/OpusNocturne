package com.xuan.opusnocturne.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OpusNocturne博客系统启动类
 *
 * @author 玄〤
 */
@SpringBootApplication
public class OpusNocturneApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpusNocturneApplication.class, args);
        System.out.println("""

                ╔═══════════════════════════════════════════════════════════════╗
                ║                                                               ║
                ║   ██████╗ ██████╗ ██╗   ██╗███████╗                          ║
                ║  ██╔═══██╗██╔══██╗██║   ██║██╔════╝                          ║
                ║  ██║   ██║██████╔╝██║   ██║███████╗                          ║
                ║  ██║   ██║██╔═══╝ ██║   ██║╚════██║                          ║
                ║  ╚██████╔╝██║     ╚██████╔╝███████║                          ║
                ║   ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝                          ║
                ║                                                               ║
                ║  ███╗   ██╗ ██████╗  ██████╗████████╗██╗   ██╗██████╗ ███╗  ║
                ║  ████╗  ██║██╔═══██╗██╔════╝╚══██╔══╝██║   ██║██╔══██╗████║ ║
                ║  ██╔██╗ ██║██║   ██║██║        ██║   ██║   ██║██████╔╝██╔██║║
                ║  ██║╚██╗██║██║   ██║██║        ██║   ██║   ██║██╔══██╗██║╚██║║
                ║  ██║ ╚████║╚██████╔╝╚██████╗   ██║   ╚██████╔╝██║  ██║██║ ╚█║║
                ║  ╚═╝  ╚═══╝ ╚═════╝  ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚╝║
                ║                                                               ║
                ║  OpusNocturne博客系统启动成功！                                ║
                ║  API文档地址: http://localhost:8080/doc.html                  ║
                ║  作者: 玄〤                                                    ║
                ║  版本: 1.0.0                                                  ║
                ║                                                               ║
                ╚═══════════════════════════════════════════════════════════════╝
                """);
    }
}
