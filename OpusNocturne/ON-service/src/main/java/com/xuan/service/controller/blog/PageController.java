package com.xuan.service.controller.blog;

import com.xuan.common.domain.Result;
import com.xuan.service.service.ISysSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台页面控制器
 * 
 * @author 玄〤
 */
@Tag(name = "前台页面")
@RestController("blogPageController")
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class PageController {

    private final ISysSettingService sysSettingService;

    @Operation(summary = "获取关于我")
    @GetMapping("/about")
    public Result<String> getAbout() {
        return Result.success(sysSettingService.getSettingValue("aboutMe", "Just a blog."));
    }
}
