package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.entity.po.interact.Attachment;
import com.xuan.service.mapper.AttachmentMapper;
import com.xuan.service.service.IAttachmentService;
import org.springframework.stereotype.Service;

/**
 * 附件服务实现类
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    @Override
    public Attachment saveAttachment(String fileName, String fileUrl, String filePath,
            String fileType, Long fileSize, String bizType, Long bizId) {
        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileUrl(fileUrl);
        attachment.setFilePath(filePath);
        attachment.setFileType(fileType);
        attachment.setFileSize(fileSize);
        attachment.setBizType(bizType);
        attachment.setBizId(bizId);
        this.save(attachment);
        return attachment;
    }
}