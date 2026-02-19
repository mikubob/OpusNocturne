package com.xuan.service.controller.admin;

import com.xuan.common.domain.Result;
import com.xuan.common.utils.UploadUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadUtils uploadUtils;

    @Operation(summary = "上传文件")
    @PostMapping
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = uploadUtils.upload(file);
        return Result.success(url);
    }
}