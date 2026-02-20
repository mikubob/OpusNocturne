package com.xuan.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import com.xuan.entity.dto.comment.CommentAuditDTO;
import com.xuan.entity.dto.comment.CommentCreateDTO;
import com.xuan.entity.dto.comment.CommentPageQueryDTO;
import com.xuan.entity.po.blog.Article;
import com.xuan.entity.po.interact.Comment;
import com.xuan.entity.vo.comment.CommentAdminVO;
import com.xuan.entity.vo.comment.CommentTreeVO;
import com.xuan.service.mapper.ArticleMapper;
import com.xuan.service.mapper.CommentMapper;
import com.xuan.service.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private final ArticleMapper articleMapper;

    @Override
    public List<CommentTreeVO> getCommentTree(Long articleId) {
        // 查询该文章下所有已审核通过的评论
        List<Comment> comments = this.list(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreateTime));

        // 构建评论树
        return buildCommentTree(comments); // 假设 buildCommentTree 是私有方法，原代码里有调用
    }

    @Override
    public Map<String, Long> getArticleCommentStats(Long articleId) {
        Long total = this.lambdaQuery()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .count();
        Long rootCount = this.lambdaQuery()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .isNull(Comment::getParentId)
                .count();

        Map<String, Long> map = new java.util.HashMap<>();
        map.put("total", total != null ? total : 0L);
        map.put("rootCount", rootCount != null ? rootCount : 0L);
        map.put("replyCount", (total != null ? total : 0L) - (rootCount != null ? rootCount : 0L));
        return map;
    }

    @Override
    @Transactional
    public void createComment(CommentCreateDTO dto, String ipAddress, String userAgent) {
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId() == 0 ? null : dto.getArticleId());
        comment.setContent(dto.getContent());
        comment.setNickname(dto.getNickname());
        comment.setEmail(dto.getEmail());
        comment.setParentId(dto.getParentId());
        comment.setRootParentId(dto.getRootParentId());
        comment.setIpAddress(ipAddress);
        comment.setUserAgent(userAgent);
        comment.setStatus(0); // 默认待审核

        // 如果有父评论，获取被回复人信息
        if (dto.getParentId() != null) {
            Comment parentComment = this.getById(dto.getParentId());
            if (parentComment != null) {
                comment.setReplyUserId(parentComment.getUserId());
            }
        }

        this.save(comment);
    }

    @Override
    public Page<CommentAdminVO> pageComments(CommentPageQueryDTO dto) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (dto.getArticleId() != null) {
            wrapper.eq(Comment::getArticleId, dto.getArticleId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Comment::getStatus, dto.getStatus());
        }
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            wrapper.like(Comment::getNickname, dto.getNickname());
        }
        wrapper.orderByDesc(Comment::getCreateTime);

        // 确保分页参数不为null，提供默认值
        Integer currentPage = dto.getCurrent() != null ? dto.getCurrent() : 1;
        Integer pageSize = dto.getSize() != null ? dto.getSize() : 10;
        Page<Comment> page = this.page(new Page<>(currentPage, pageSize), wrapper);

        Page<CommentAdminVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(comment -> {
            CommentAdminVO vo = BeanUtil.copyProperties(comment, CommentAdminVO.class);
            // 填充文章标题
            if (comment.getArticleId() != null) {
                Article article = articleMapper.selectById(comment.getArticleId());
                if (article != null)
                    vo.setArticleTitle(article.getTitle());
            } else {
                vo.setArticleTitle("留言板");
            }
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void auditComment(Long id, CommentAuditDTO dto) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        comment.setStatus(dto.getStatus());
        this.updateById(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public void batchAuditComments(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "请选择要审核的评论");
        }
        this.lambdaUpdate()
                .in(Comment::getId, ids)
                .set(Comment::getStatus, status)
                .update();
    }

    @Override
    @Transactional
    public void batchDeleteComments(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "请选择要删除的评论");
        }
        this.removeByIds(ids);
    }

    /**
     * 构建评论树
     */
    private List<CommentTreeVO> buildCommentTree(List<Comment> comments) {
        // 转换为 VO
        List<CommentTreeVO> allVOs = comments.stream().map(c -> {
            CommentTreeVO vo = new CommentTreeVO();
            vo.setId(c.getId());
            vo.setNickname(c.getNickname());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            vo.setChildren(new ArrayList<>());

            // 填充被回复人昵称
            if (c.getParentId() != null) {
                Comment parent = comments.stream()
                        .filter(p -> p.getId().equals(c.getParentId()))
                        .findFirst().orElse(null);
                if (parent != null) {
                    vo.setReplyNickname(parent.getNickname());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        // 按 ID 建立索引
        Map<Long, CommentTreeVO> voMap = allVOs.stream()
                .collect(Collectors.toMap(CommentTreeVO::getId, v -> v));

        // 构建树结构
        List<CommentTreeVO> roots = new ArrayList<>();
        for (Comment c : comments) {
            CommentTreeVO vo = voMap.get(c.getId());
            if (c.getRootParentId() == null && c.getParentId() == null) {
                // 顶级评论
                roots.add(vo);
            } else {
                // 子评论挂到根评论下
                Long rootId = c.getRootParentId() != null ? c.getRootParentId() : c.getParentId();
                CommentTreeVO rootVO = voMap.get(rootId);
                if (rootVO != null) {
                    rootVO.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        return roots;
    }
}