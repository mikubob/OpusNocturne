package com.xuan.service.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.common.domain.Result;
import com.xuan.entity.dto.system.AttachmentPageQueryDTO;
import com.xuan.entity.po.interact.Attachment;
import com.xuan.service.service.IAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 附件管理控制器
 * 
 * @author 玄〤
 */
@Tag(name = "附件管理")
@RestController("adminAttachmentController")
@RequestMapping("/api/admin/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final IAttachmentService attachmentService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<Page<Attachment>> pageAttachment(@Validated AttachmentPageQueryDTO dto) {
        LambdaQueryWrapper<Attachment> queryWrapper = new LambdaQueryWrapper<>();
        if (dto.getFileName() != null && !dto.getFileName().isEmpty()) {
            queryWrapper.like(Attachment::getFileName, dto.getFileName());
        }
        if (dto.getFileType() != null && !dto.getFileType().isEmpty()) {
            queryWrapper.eq(Attachment::getFileType, dto.getFileType());
        }
        queryWrapper.orderByDesc(Attachment::getCreateTime);

        return Result.success(attachmentService.page(
                new Page<>(dto.getCurrent(), dto.getSize()), queryWrapper));
    }

    @Operation(summary = "删除附件")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.removeById(id);
        return Result.success();
    }
}
