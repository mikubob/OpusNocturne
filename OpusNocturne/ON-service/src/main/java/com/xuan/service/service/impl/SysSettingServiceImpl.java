package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.constant.RedisConstant;
import com.xuan.entity.dto.system.SystemSettingDTO;
import com.xuan.entity.po.sys.SysSetting;
import com.xuan.entity.vo.system.SystemSettingVO;
import com.xuan.service.mapper.SysSettingMapper;
import com.xuan.service.service.ISysSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;

import java.util.concurrent.TimeUnit;

/**
 * 系统设置服务实现（DB + Redis 双层缓存，Cache Aside Pattern）
 *
 * 读取策略：优先从 Redis 获取缓存，未命中则查询数据库并回填缓存
 * 写入策略：先更新数据库，再删除 Redis 缓存，下次读取时自动重建
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class SysSettingServiceImpl extends ServiceImpl<SysSettingMapper, SysSetting>
        implements ISysSettingService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 获取系统设置（优先读 Redis 缓存）
     */
    @Override
    public SystemSettingVO getSettings() {
        // 1. 尝试从 Redis 获取缓存
        String cached = redisTemplate.opsForValue().get(RedisConstant.SYS_SETTING_CACHE_KEY);
        if (cached != null) {
            return JSON.parseObject(cached, SystemSettingVO.class);
        }

        // 2. 缓存未命中，查询数据库（取第一条记录作为全局设置）
        SysSetting setting = lambdaQuery().last("LIMIT 1").one();
        if (setting == null) {
            return new SystemSettingVO();
        }

        // 3. 转换为 VO
        SystemSettingVO vo = toVO(setting);

        // 4. 回填 Redis 缓存
        redisTemplate.opsForValue().set(
                RedisConstant.SYS_SETTING_CACHE_KEY,
                JSON.toJSONString(vo),
                RedisConstant.SYS_SETTING_TTL_HOURS,
                TimeUnit.HOURS);

        return vo;
    }

    /**
     * 更新系统设置（写 DB + 清除 Redis 缓存）
     */
    @Override
    public void updateSettings(SystemSettingDTO dto) {
        // 1. 查询现有设置
        SysSetting setting = lambdaQuery().last("LIMIT 1").one();
        if (setting == null) {
            setting = new SysSetting();
        }

        // 2. 使用 DTO 更新字段（仅更新非 null 字段）
        if (dto.getSiteName() != null)
            setting.setSiteName(dto.getSiteName());
        if (dto.getSiteDescription() != null)
            setting.setSiteDescription(dto.getSiteDescription());
        if (dto.getSiteKeywords() != null)
            setting.setSiteKeywords(dto.getSiteKeywords());
        if (dto.getFooterText() != null)
            setting.setFooterText(dto.getFooterText());
        if (dto.getAdminEmail() != null)
            setting.setAdminEmail(dto.getAdminEmail());
        if (dto.getCommentAudit() != null)
            setting.setCommentAudit(dto.getCommentAudit() ? 1 : 0);
        if (dto.getArticlePageSize() != null)
            setting.setArticlePageSize(dto.getArticlePageSize());
        if (dto.getCommentPageSize() != null)
            setting.setCommentPageSize(dto.getCommentPageSize());
        if (dto.getAboutMe() != null)
            setting.setAboutMe(dto.getAboutMe());

        // 3. 保存或更新数据库
        saveOrUpdate(setting);

        // 4. 删除 Redis 缓存（下次读取时自动重建）
        redisTemplate.delete(RedisConstant.SYS_SETTING_CACHE_KEY);
    }

    /**
     * 根据配置键获取配置值
     */
    @Override
    public String getSettingValue(String key, String defaultValue) {
        SystemSettingVO settings = getSettings();
        return switch (key) {
            case "siteName" -> settings.getSiteName() != null ? settings.getSiteName() : defaultValue;
            case "siteDescription" ->
                settings.getSiteDescription() != null ? settings.getSiteDescription() : defaultValue;
            case "siteKeywords" -> settings.getSiteKeywords() != null ? settings.getSiteKeywords() : defaultValue;
            case "footerText" -> settings.getFooterText() != null ? settings.getFooterText() : defaultValue;
            case "adminEmail" -> settings.getAdminEmail() != null ? settings.getAdminEmail() : defaultValue;
            case "aboutMe" -> settings.getAboutMe() != null ? settings.getAboutMe() : defaultValue;
            default -> defaultValue;
        };
    }

    /**
     * PO → VO 转换
     */
    private SystemSettingVO toVO(SysSetting setting) {
        SystemSettingVO vo = new SystemSettingVO();
        vo.setSiteName(setting.getSiteName());
        vo.setSiteDescription(setting.getSiteDescription());
        vo.setSiteKeywords(setting.getSiteKeywords());
        vo.setFooterText(setting.getFooterText());
        vo.setAdminEmail(setting.getAdminEmail());
        vo.setCommentAudit(setting.getCommentAudit() != null && setting.getCommentAudit() == 1);
        vo.setArticlePageSize(setting.getArticlePageSize());
        vo.setCommentPageSize(setting.getCommentPageSize());
        vo.setAboutMe(setting.getAboutMe());
        return vo;
    }
}
