package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.entity.*;
import com.citystray.mapper.*;
import com.citystray.service.ContentService;
import com.citystray.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {

    private final ContentBannerMapper bannerMapper;
    private final ContentStoryMapper storyMapper;
    private final ContentNoticeMapper noticeMapper;
    private final ContentReportMapper reportMapper;
    private final ContentArticleMapper articleMapper;

    // ==================== Banner ====================

    @Override
    public List<ContentBanner> bannerList() {
        LambdaQueryWrapper<ContentBanner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ContentBanner::getSort);
        return bannerMapper.selectList(wrapper);
    }

    @Override
    public void saveBanner(ContentBanner banner) {
        if (banner.getId() != null) {
            bannerMapper.updateById(banner);
        } else {
            bannerMapper.insert(banner);
        }
    }

    @Override
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBannerSort(List<Map<String, Object>> sortList) {
        for (Map<String, Object> item : sortList) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer sort = Integer.valueOf(item.get("sort").toString());
            ContentBanner banner = new ContentBanner();
            banner.setId(id);
            banner.setSort(sort);
            bannerMapper.updateById(banner);
        }
    }

    // ==================== 故事 ====================

    @Override
    public PageResult<ContentStory> storyList(Integer page, Integer pageSize, String status) {
        LambdaQueryWrapper<ContentStory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(ContentStory::getStatus, status);
        }
        wrapper.orderByDesc(ContentStory::getCreateTime);
        Page<ContentStory> pageObj = storyMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(pageObj.getTotal(), pageObj.getRecords());
    }

    @Override
    public void reviewStory(Long id, String status) {
        ContentStory story = new ContentStory();
        story.setId(id);
        story.setStatus(status);
        storyMapper.updateById(story);
    }

    @Override
    public void saveStory(ContentStory story) {
        // 小程序用户发布故事，自动填充作者和状态
        if (!StringUtils.hasText(story.getAuthorName())) {
            String username = UserContext.getUsername();
            if (username != null) {
                story.setAuthorName(username);
            }
        }
        Long userId = UserContext.getUserId();
        if (userId != null) {
            story.setUserId(userId);
        }
        if (!StringUtils.hasText(story.getStatus())) {
            story.setStatus("PENDING");
        }
        if (story.getViewCount() == null) {
            story.setViewCount(0);
        }
        storyMapper.insert(story);
    }

    // ==================== 公告 ====================

    @Override
    public PageResult<ContentNotice> noticeList(Integer page, Integer pageSize) {
        LambdaQueryWrapper<ContentNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ContentNotice::getCreateTime);
        Page<ContentNotice> pageObj = noticeMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(pageObj.getTotal(), pageObj.getRecords());
    }

    @Override
    public void saveNotice(ContentNotice notice) {
        // 如果状态是发布且是新记录，设置发布时间
        if ("PUBLISHED".equals(notice.getStatus()) && notice.getPublishTime() == null) {
            notice.setPublishTime(LocalDateTime.now());
        }
        if (notice.getId() != null) {
            // 更新时如果从草稿变为发布，设置发布时间
            if ("PUBLISHED".equals(notice.getStatus())) {
                ContentNotice existing = noticeMapper.selectById(notice.getId());
                if (existing != null && existing.getPublishTime() == null) {
                    notice.setPublishTime(LocalDateTime.now());
                }
            }
            noticeMapper.updateById(notice);
        } else {
            noticeMapper.insert(notice);
        }
    }

    @Override
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
    }

    // ==================== 举报 ====================

    @Override
    public PageResult<ContentReport> reportList(Integer page, Integer pageSize, String status) {
        LambdaQueryWrapper<ContentReport> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(ContentReport::getStatus, status);
        }
        wrapper.orderByDesc(ContentReport::getCreateTime);
        Page<ContentReport> pageObj = reportMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(pageObj.getTotal(), pageObj.getRecords());
    }

    @Override
    public void handleReport(Long id, String status) {
        ContentReport report = new ContentReport();
        report.setId(id);
        report.setStatus(status);
        report.setHandleTime(LocalDateTime.now());
        // 从 UserContext 获取处理人
        String username = UserContext.getUsername();
        if (username != null) {
            report.setHandlerName(username);
        }
        reportMapper.updateById(report);
    }

    @Override
    public void submitReport(ContentReport report) {
        // 小程序用户提交举报，自动填充举报人信息
        Long userId = UserContext.getUserId();
        if (userId != null) {
            report.setReporterId(userId);
        }
        String username = UserContext.getUsername();
        if (username != null && !StringUtils.hasText(report.getReporterName())) {
            report.setReporterName(username);
        }
        if (!StringUtils.hasText(report.getStatus())) {
            report.setStatus("PENDING");
        }
        reportMapper.insert(report);
    }

    // ==================== 知识科普 ====================

    @Override
    public PageResult<ContentArticle> articleList(Integer page, Integer pageSize, String keyword, String category) {
        LambdaQueryWrapper<ContentArticle> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ContentArticle::getTitle, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(ContentArticle::getCategory, category);
        }
        wrapper.orderByDesc(ContentArticle::getCreateTime);
        Page<ContentArticle> pageObj = articleMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(pageObj.getTotal(), pageObj.getRecords());
    }

    @Override
    public void saveArticle(ContentArticle article) {
        if ("PUBLISHED".equals(article.getStatus()) && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        if (article.getId() != null) {
            if ("PUBLISHED".equals(article.getStatus())) {
                ContentArticle existing = articleMapper.selectById(article.getId());
                if (existing != null && existing.getPublishTime() == null) {
                    article.setPublishTime(LocalDateTime.now());
                }
            }
            articleMapper.updateById(article);
        } else {
            // 新文章默认作者为当前用户
            if (!StringUtils.hasText(article.getAuthor())) {
                String username = UserContext.getUsername();
                if (username != null) {
                    article.setAuthor(username);
                }
            }
            articleMapper.insert(article);
        }
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void toggleArticleStatus(Long id) {
        ContentArticle article = articleMapper.selectById(id);
        if (article != null) {
            if ("PUBLISHED".equals(article.getStatus())) {
                article.setStatus("DRAFT");
            } else {
                article.setStatus("PUBLISHED");
                if (article.getPublishTime() == null) {
                    article.setPublishTime(LocalDateTime.now());
                }
            }
            articleMapper.updateById(article);
        }
    }
}
