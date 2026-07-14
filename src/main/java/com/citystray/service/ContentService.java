package com.citystray.service;

import com.citystray.common.PageResult;
import com.citystray.entity.*;

import java.util.List;
import java.util.Map;

public interface ContentService {

    // ========== Banner ==========
    List<ContentBanner> bannerList();
    void saveBanner(ContentBanner banner);
    void deleteBanner(Long id);
    void updateBannerSort(List<Map<String, Object>> sortList);

    // ========== 故事 ==========
    PageResult<ContentStory> storyList(Integer page, Integer pageSize, String status);
    void reviewStory(Long id, String status);
    void saveStory(ContentStory story);

    // ========== 公告 ==========
    PageResult<ContentNotice> noticeList(Integer page, Integer pageSize);
    void saveNotice(ContentNotice notice);
    void deleteNotice(Long id);

    // ========== 举报 ==========
    PageResult<ContentReport> reportList(Integer page, Integer pageSize, String status);
    void handleReport(Long id, String status);
    void submitReport(ContentReport report);

    // ========== 知识科普 ==========
    PageResult<ContentArticle> articleList(Integer page, Integer pageSize, String keyword, String category);
    void saveArticle(ContentArticle article);
    void deleteArticle(Long id);
    void toggleArticleStatus(Long id);
}
