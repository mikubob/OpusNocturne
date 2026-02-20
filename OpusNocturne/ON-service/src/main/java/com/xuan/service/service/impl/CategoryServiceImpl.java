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
import com.xuan.entity.dto.category.CategoryCreateDTO;
import com.xuan.entity.dto.category.CategoryPageQueryDTO;
import com.xuan.entity.dto.category.CategoryUpdateDTO;
import com.xuan.entity.po.blog.Article;
import com.xuan.entity.po.blog.Category;
import com.xuan.entity.vo.category.CategoryAdminVO;
import com.xuan.entity.vo.category.CategoryVO;
import com.xuan.service.mapper.ArticleMapper;
import com.xuan.service.mapper.CategoryMapper;
import com.xuan.service.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 前台分类列表使用 Redis 缓存，后台操作时主动清除缓存
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final ArticleMapper articleMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<CategoryVO> listAllCategories() {
        // 1. 尝试从 Redis 读取缓存
        String cached = redisTemplate.opsForValue().get(RedisConstant.CATEGORY_LIST_KEY);
        if (cached != null) {
            return JSON.parseObject(cached, new TypeReference<List<CategoryVO>>() {
            });
        }

        // 2. 缓存未命中，查询数据库
        List<Category> categories = this.list(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort));

        List<CategoryVO> voList = categories.stream().map(category -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            // 统计该分类下已发布的文章数
            Long count = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>()
                            .eq(Article::getCategoryId, category.getId())
                            .eq(Article::getStatus, 1));
            vo.setArticleCount(count.intValue());
            return vo;
        }).collect(Collectors.toList());

        // 3. 回填缓存
        redisTemplate.opsForValue().set(RedisConstant.CATEGORY_LIST_KEY,
                JSON.toJSONString(voList),
                RedisConstant.CATEGORY_TAG_TTL_HOURS, TimeUnit.HOURS);

        return voList;
    }

    @Override
    public Page<CategoryAdminVO> pageCategories(CategoryPageQueryDTO queryDTO) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Category::getName, queryDTO.getName());
        }
        wrapper.orderByAsc(Category::getSort);

        // 确保分页参数不为null，提供默认值
        Integer currentPage = queryDTO.getCurrent() != null ? queryDTO.getCurrent() : 1;
        Integer pageSize = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
        Page<Category> page = this.page(
                new Page<>(currentPage, pageSize),
                wrapper);

        Page<CategoryAdminVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<CategoryAdminVO> voList = page.getRecords().stream()
                .map(c -> BeanUtil.copyProperties(c, CategoryAdminVO.class))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void createCategory(CategoryCreateDTO createDTO) {
        long count = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, createDTO.getName()));
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = BeanUtil.copyProperties(createDTO, Category.class);
        if (category.getSort() == null)
            category.setSort(0);
        if (category.getStatus() == null)
            category.setStatus(1);
        this.save(category);

        // 清除缓存
        redisTemplate.delete(RedisConstant.CATEGORY_LIST_KEY);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryUpdateDTO updateDTO) {
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        long count = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, updateDTO.getName())
                .ne(Category::getId, id));
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }
        BeanUtil.copyProperties(updateDTO, category);
        this.updateById(category);

        // 清除缓存
        redisTemplate.delete(RedisConstant.CATEGORY_LIST_KEY);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        long articleCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, id));
        if (articleCount > 0) {
            throw new BusinessException("该分类下有文章，无法删除");
        }
        this.removeById(id);

        // 清除缓存
        redisTemplate.delete(RedisConstant.CATEGORY_LIST_KEY);
    }

    @Override
    @Transactional
    public void batchDeleteCategories(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "请选择要删除的分类");
        }
        // 检查每个分类下是否有文章
        for (Long id : ids) {
            long articleCount = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, id));
            if (articleCount > 0) {
                Category cat = this.getById(id);
                String name = cat != null ? cat.getName() : String.valueOf(id);
                throw new BusinessException(ErrorCode.CATEGORY_HAS_ARTICLES.getCode(),
                        "分类【" + name + "】下存在文章，无法删除");
            }
        }
        this.removeByIds(ids);

        // 清除缓存
        redisTemplate.delete(RedisConstant.CATEGORY_LIST_KEY);
    }
}