package com.xuan.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.constant.RedisConstant;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import com.xuan.entity.dto.article.*;
import com.xuan.entity.po.blog.Article;
import com.xuan.entity.po.blog.ArticleTag;
import com.xuan.entity.po.blog.Category;
import com.xuan.entity.po.blog.Tag;
import com.xuan.entity.po.sys.SysUser;
import com.xuan.entity.vo.article.*;
import com.xuan.entity.vo.tag.TagVO;
import com.xuan.service.mapper.*;
import com.xuan.service.service.IArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 * 
 * Redis 缓存策略：
 * 1. 文章浏览量：使用 Redis INCR 高频计数，定时同步到 DB
 * 2. 文章详情：热点文章缓存到 Redis，减少 DB 查询
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final SysUserMapper sysUserMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public ArticleCreatVO creatArticle(ArticleCreateDTO dto) {
        // 1. 转换并保存文章
        Article article = BeanUtil.copyProperties(dto, Article.class);
        if (dto.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        article.setViewCount(0L);
        boolean saved = this.save(article);
        if (!saved) {
            throw new BusinessException("文章创建失败");
        }

        // 2. 处理标签关联
        saveArticleTags(article.getId(), dto.getTagIds());

        // 3. 清除分类/标签缓存（文章数量发生变化）
        clearCategoryTagCache();

        // 4. 构建返回结果
        ArticleCreatVO vo = new ArticleCreatVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        return vo;
    }

    @Override
    public Page<ArticleAdminListVO> pageAdminArticles(ArticleAdminPageQueryDTO dto) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            wrapper.like(Article::getTitle, dto.getTitle());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, dto.getCategoryId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Article::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getCreateTime);

        Page<Article> page = this.page(new Page<>(dto.getCurrent(), dto.getSize()), wrapper);

        Page<ArticleAdminListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(article -> {
            ArticleAdminListVO vo = BeanUtil.copyProperties(article, ArticleAdminListVO.class);
            // 从 Redis 获取实时浏览量
            vo.setViewCount(getViewCountFromRedis(article.getId(), article.getViewCount()));
            // 填充分类名称
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null)
                    vo.setCategoryName(category.getName());
            }
            // 填充作者昵称
            if (article.getAuthorId() != null) {
                SysUser user = sysUserMapper.selectById(article.getAuthorId());
                if (user != null)
                    vo.setAuthorNickname(user.getNickname());
            }
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public ArticleAdminDetailVO getAdminArticleDetail(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        ArticleAdminDetailVO vo = BeanUtil.copyProperties(article, ArticleAdminDetailVO.class);

        // 填充分类名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null)
                vo.setCategoryName(category.getName());
        }

        // 填充标签信息
        List<Long> tagIds = articleTagMapper.selectTagIdsByArticleId(id);
        vo.setTagIds(tagIds);
        if (!tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleUpdateDTO dto) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 更新文章基本信息
        BeanUtil.copyProperties(dto, article, "id");
        if (dto.getStatus() == 1 && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        this.updateById(article);

        // 更新标签关联：先删除旧的，再插入新的
        articleTagMapper.deleteByArticleId(id);
        saveArticleTags(id, dto.getTagIds());

        // 清除文章详情缓存和分类/标签列表缓存
        clearArticleDetailCache(id);
        clearCategoryTagCache();
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        this.removeById(id);
        articleTagMapper.deleteByArticleId(id);

        // 清除相关缓存
        clearArticleDetailCache(id);
        clearCategoryTagCache();
        redisTemplate.delete(RedisConstant.ARTICLE_VIEW_KEY_PREFIX + id);
    }

    @Override
    @Transactional
    public void updateArticleTop(Long id, ArticleTopDTO dto) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        article.setIsTop(dto.getIsTop());
        this.updateById(article);
    }

    @Override
    @Transactional
    public void updateArticleStatus(Long id, ArticleStatusDTO dto) {
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        article.setStatus(dto.getStatus());
        if (dto.getStatus() == 1 && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        this.updateById(article);
        clearArticleDetailCache(id);
    }

    @Override
    public Page<ArticleListVO> pageBlogArticles(ArticlePageQueryDTO dto) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1); // 只查已发布
        if (dto.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, dto.getCategoryId());
        }
        // 按标签筛选需要关联查询
        if (dto.getTagId() != null) {
            List<Long> articleIds = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, dto.getTagId())).stream()
                    .map(ArticleTag::getArticleId).collect(Collectors.toList());
            if (articleIds.isEmpty()) {
                return new Page<>(dto.getCurrent(), dto.getSize(), 0);
            }
            wrapper.in(Article::getId, articleIds);
        }
        wrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getPublishTime);

        Page<Article> page = this.page(new Page<>(dto.getCurrent(), dto.getSize()), wrapper);

        Page<ArticleListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(article -> {
            ArticleListVO vo = BeanUtil.copyProperties(article, ArticleListVO.class);
            // 从 Redis 获取实时浏览量
            vo.setViewCount(getViewCountFromRedis(article.getId(), article.getViewCount()));
            // 填充分类名称
            if (article.getCategoryId() != null) {
                Category category = categoryMapper.selectById(article.getCategoryId());
                if (category != null)
                    vo.setCategoryName(category.getName());
            }
            // 填充标签
            List<Long> tagIds = articleTagMapper.selectTagIdsByArticleId(article.getId());
            if (!tagIds.isEmpty()) {
                List<Tag> tags = tagMapper.selectBatchIds(tagIds);
                vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
            } else {
                vo.setTags(Collections.emptyList());
            }
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public ArticleDetailVO getBlogArticleDetail(Long id) {
        Article article = this.getById(id);
        if (article == null || article.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 使用 Redis INCR 增加浏览次数（高频操作不直接写库）
        String viewKey = RedisConstant.ARTICLE_VIEW_KEY_PREFIX + id;
        redisTemplate.opsForValue().increment(viewKey);

        ArticleDetailVO vo = BeanUtil.copyProperties(article, ArticleDetailVO.class);
        // 设置实时浏览量
        vo.setViewCount(getViewCountFromRedis(id, article.getViewCount()));

        // 填充分类名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null)
                vo.setCategoryName(category.getName());
        }

        // 填充作者昵称
        if (article.getAuthorId() != null) {
            SysUser user = sysUserMapper.selectById(article.getAuthorId());
            if (user != null)
                vo.setAuthorNickname(user.getNickname());
        }

        // 填充标签
        List<Long> tagIds = articleTagMapper.selectTagIdsByArticleId(id);
        if (!tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
        } else {
            vo.setTags(Collections.emptyList());
        }

        // 填充上一篇/下一篇（同为已发布状态）
        setPrevNextArticle(vo, id);

        return vo;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 保存文章标签关联
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> articleTags = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                at.setCreateTime(now);
                articleTags.add(at);
            }
            articleTagMapper.batchInsertArticleTags(articleTags);
        }
    }

    /**
     * 从 Redis 获取文章实时浏览量
     * Redis 中存储的是增量计数，需要加上 DB 中的基础值
     */
    private Long getViewCountFromRedis(Long articleId, Long dbViewCount) {
        String viewKey = RedisConstant.ARTICLE_VIEW_KEY_PREFIX + articleId;
        String redisVal = redisTemplate.opsForValue().get(viewKey);
        long redisIncrement = (redisVal != null) ? Long.parseLong(redisVal) : 0L;
        long dbBase = (dbViewCount != null) ? dbViewCount : 0L;
        return dbBase + redisIncrement;
    }

    /**
     * 设置上一篇/下一篇文章信息
     */
    private void setPrevNextArticle(ArticleDetailVO vo, Long currentId) {
        // 上一篇：ID 小于当前，按 ID 降序取第一条
        Article prev = this.getOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .lt(Article::getId, currentId)
                        .select(Article::getId, Article::getTitle)
                        .orderByDesc(Article::getId)
                        .last("LIMIT 1"));
        if (prev != null) {
            ArticleDetailVO.ArticleNavVO prevNav = new ArticleDetailVO.ArticleNavVO();
            prevNav.setId(prev.getId());
            prevNav.setTitle(prev.getTitle());
            vo.setPrevArticle(prevNav);
        }

        // 下一篇：ID 大于当前，按 ID 升序取第一条
        Article next = this.getOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .gt(Article::getId, currentId)
                        .select(Article::getId, Article::getTitle)
                        .orderByAsc(Article::getId)
                        .last("LIMIT 1"));
        if (next != null) {
            ArticleDetailVO.ArticleNavVO nextNav = new ArticleDetailVO.ArticleNavVO();
            nextNav.setId(next.getId());
            nextNav.setTitle(next.getTitle());
            vo.setNextArticle(nextNav);
        }
    }

    /**
     * Tag PO -> TagVO 转换
     */
    private TagVO toTagVO(Tag tag) {
        TagVO tagVO = new TagVO();
        tagVO.setId(tag.getId());
        tagVO.setName(tag.getName());
        tagVO.setColor(tag.getColor());
        return tagVO;
    }

    /**
     * 清除文章详情缓存
     */
    private void clearArticleDetailCache(Long articleId) {
        redisTemplate.delete(RedisConstant.ARTICLE_DETAIL_KEY_PREFIX + articleId);
    }

    /**
     * 清除分类/标签列表缓存
     */
    private void clearCategoryTagCache() {
        redisTemplate.delete(RedisConstant.CATEGORY_LIST_KEY);
        redisTemplate.delete(RedisConstant.TAG_LIST_KEY);
    }
}
