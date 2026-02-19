package com.xuan.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.constant.RedisConstant;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import com.xuan.entity.dto.tag.TagDTO;
import com.xuan.entity.dto.tag.TagPageQueryDTO;
import com.xuan.entity.po.blog.ArticleTag;
import com.xuan.entity.po.blog.Tag;
import com.xuan.entity.vo.tag.TagAdminVO;
import com.xuan.entity.vo.tag.TagVO;
import com.xuan.service.mapper.ArticleTagMapper;
import com.xuan.service.mapper.TagMapper;
import com.xuan.service.service.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 * 前台标签列表使用 Redis 缓存，后台操作时主动清除缓存
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    private final ArticleTagMapper articleTagMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<TagVO> listAllTags() {
        // 1. 尝试从 Redis 读取缓存
        String cached = redisTemplate.opsForValue().get(RedisConstant.TAG_LIST_KEY);
        if (cached != null) {
            return JSON.parseObject(cached, new TypeReference<List<TagVO>>() {
            });
        }

        // 2. 缓存未命中，查询数据库
        List<Tag> tags = this.list(new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getId));
        List<TagVO> voList = tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setColor(tag.getColor());
            Long count = articleTagMapper.selectCount(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tag.getId()));
            vo.setArticleCount(count.intValue());
            return vo;
        }).collect(Collectors.toList());

        // 3. 回填缓存
        redisTemplate.opsForValue().set(RedisConstant.TAG_LIST_KEY,
                JSON.toJSONString(voList),
                RedisConstant.CATEGORY_TAG_TTL_HOURS, TimeUnit.HOURS);

        return voList;
    }

    @Override
    public Page<TagAdminVO> pageTags(TagPageQueryDTO queryDTO) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Tag::getName, queryDTO.getName());
        }
        wrapper.orderByDesc(Tag::getId);

        Page<Tag> page = this.page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);

        Page<TagAdminVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(t -> BeanUtil.copyProperties(t, TagAdminVO.class))
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void createTag(TagDTO tagDTO) {
        long count = this.count(new LambdaQueryWrapper<Tag>().eq(Tag::getName, tagDTO.getName()));
        if (count > 0) {
            throw new BusinessException("标签名称已存在");
        }
        Tag tag = BeanUtil.copyProperties(tagDTO, Tag.class);
        this.save(tag);

        // 清除缓存
        redisTemplate.delete(RedisConstant.TAG_LIST_KEY);
    }

    @Override
    @Transactional
    public void updateTag(Long id, TagDTO tagDTO) {
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new BusinessException(ErrorCode.TAG_NOT_FOUND);
        }
        long count = this.count(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, tagDTO.getName()).ne(Tag::getId, id));
        if (count > 0) {
            throw new BusinessException("标签名称已存在");
        }
        BeanUtil.copyProperties(tagDTO, tag);
        this.updateById(tag);

        // 清除缓存
        redisTemplate.delete(RedisConstant.TAG_LIST_KEY);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new BusinessException(ErrorCode.TAG_NOT_FOUND);
        }
        long articleCount = articleTagMapper.selectCount(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, id));
        if (articleCount > 0) {
            throw new BusinessException("该标签有关联文章，无法删除");
        }
        this.removeById(id);

        // 清除缓存
        redisTemplate.delete(RedisConstant.TAG_LIST_KEY);
    }
}