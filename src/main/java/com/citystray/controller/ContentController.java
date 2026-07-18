package com.citystray.controller;

import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.*;
import com.citystray.service.ContentService;
import com.citystray.service.NotificationService;
import com.citystray.entity.ContentStory;
import com.citystray.mapper.ContentStoryMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "内容管理")
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final NotificationService notificationService;
    private final ContentStoryMapper contentStoryMapper;

    // ========== Banner管理 ==========

    @ApiOperation("Banner列表")
    @GetMapping("/banner/list")
    public Result<List<ContentBanner>> bannerList() {
        return Result.success(contentService.bannerList());
    }

    @ApiOperation("保存Banner")
    @PostMapping("/banner/save")
    public Result<?> saveBanner(@RequestBody ContentBanner banner) {
        contentService.saveBanner(banner);
        return Result.success();
    }

    @ApiOperation("删除Banner")
    @DeleteMapping("/banner/{id}")
    public Result<?> deleteBanner(@PathVariable Long id) {
        contentService.deleteBanner(id);
        return Result.success();
    }

    @ApiOperation("更新Banner排序")
    @PutMapping("/banner/sort")
    public Result<?> updateBannerSort(@RequestBody List<Map<String, Object>> sortList) {
        contentService.updateBannerSort(sortList);
        return Result.success();
    }

    // ========== 救助故事 ==========

    @ApiOperation("故事列表")
    @GetMapping("/story/list")
    public Result<PageResult<ContentStory>> storyList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        return Result.success(contentService.storyList(page, pageSize, status));
    }

    @ApiOperation("审核故事")
    @PostMapping("/story/{id}/review")
    public Result<?> reviewStory(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        contentService.reviewStory(id, status);

        // 通知故事作者审核结果
        try {
            ContentStory story = contentStoryMapper.selectById(id);
            if (story != null && story.getUserId() != null) {
                String statusText = "APPROVED".equals(status) ? "已通过" : "REJECTED".equals(status) ? "未通过" : status;
                notificationService.sendNotification(
                    story.getUserId(),
                    "故事审核通知",
                    "您发布的故事《" + story.getTitle() + "》审核结果：" + statusText,
                    "STORY", "STORY", id
                );
            }
        } catch (Exception e) {
            // 通知失败不影响主流程
        }

        return Result.success();
    }

    @ApiOperation("发布救助故事（小程序端）")
    @PostMapping("/story/save")
    public Result<?> saveStory(@RequestBody ContentStory story) {
        contentService.saveStory(story);
        return Result.success(story.getId());
    }

    // ========== 公告管理 ==========

    @ApiOperation("公告列表")
    @GetMapping("/notice/list")
    public Result<PageResult<ContentNotice>> noticeList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(contentService.noticeList(page, pageSize));
    }

    @ApiOperation("保存公告")
    @PostMapping("/notice/save")
    public Result<?> saveNotice(@RequestBody ContentNotice notice) {
        contentService.saveNotice(notice);
        return Result.success();
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/notice/{id}")
    public Result<?> deleteNotice(@PathVariable Long id) {
        contentService.deleteNotice(id);
        return Result.success();
    }

    // ========== 内容举报 ==========

    @ApiOperation("举报列表")
    @GetMapping("/report/list")
    public Result<PageResult<ContentReport>> reportList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        return Result.success(contentService.reportList(page, pageSize, status));
    }

    @ApiOperation("处理举报")
    @PostMapping("/report/{id}/handle")
    public Result<?> handleReport(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        contentService.handleReport(id, status);
        return Result.success();
    }

    @ApiOperation("提交举报（小程序端）")
    @PostMapping("/report/save")
    public Result<?> submitReport(@RequestBody ContentReport report) {
        contentService.submitReport(report);
        return Result.success(report.getId());
    }

    // ========== 知识科普 ==========

    @ApiOperation("文章列表")
    @GetMapping("/article/list")
    public Result<PageResult<ContentArticle>> articleList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("分类") @RequestParam(required = false) String category) {
        return Result.success(contentService.articleList(page, pageSize, keyword, category));
    }

    @ApiOperation("保存文章")
    @PostMapping("/article/save")
    public Result<?> saveArticle(@RequestBody ContentArticle article) {
        contentService.saveArticle(article);
        return Result.success();
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/article/{id}")
    public Result<?> deleteArticle(@PathVariable Long id) {
        contentService.deleteArticle(id);
        return Result.success();
    }

    @ApiOperation("切换文章发布状态")
    @PostMapping("/article/{id}/toggle")
    public Result<?> toggleArticleStatus(@PathVariable Long id) {
        contentService.toggleArticleStatus(id);
        return Result.success();
    }
}
