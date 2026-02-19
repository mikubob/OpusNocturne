package com.xuan.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.entity.po.interact.Attachment;

/**
 * 附件服务接口
 *
 * @author 玄〤
 * @since 2026-02-20
 */
public interface IAttachmentService extends IService<Attachment> {

    /**
     * 保存附件记录
     */
    Attachment saveAttachment(String fileName, String fileUrl, String filePath,
            String fileType, Long fileSize, String bizType, Long bizId);
}