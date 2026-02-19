package com.xuan.service.controller.admin;

import com.xuan.common.domain.Result;
import com.xuan.entity.dto.system.SystemSettingDTO;
import com.xuan.service.service.ISysSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 关于我管理控制器
 * 
 * @author 玄〤
 */
@Tag(name = "关于我管理")
@RestController("adminAboutController")
@RequestMapping("/api/admin/about")
@RequiredArgsConstructor
public class AboutController {

    private final ISysSettingService sysSettingService;

    @Operation(summary = "更新关于我")
    @PutMapping
    public Result<Void> updateAbout(@RequestBody SystemSettingDTO dto) {
        sysSettingService.updateSettings(dto);
        return Result.success();
    }
}
