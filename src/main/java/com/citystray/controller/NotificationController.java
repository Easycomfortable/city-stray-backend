package com.citystray.controller;

import com.citystray.annotation.RequireRole;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.SysNotification;
import com.citystray.service.NotificationService;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Api(tags = "消息通知")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation("通知列表")
    @GetMapping("/list")
    public Result<PageResult<SysNotification>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("仅未读") @RequestParam(required = false) Boolean unreadOnly) {
        Long userId = UserContext.getUserId();
        return Result.success(notificationService.getNotificationList(userId, page, pageSize, unreadOnly));
    }

    @ApiOperation("未读数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> unreadCount() {
        Long userId = UserContext.getUserId();
        int count = notificationService.getUnreadCount(userId);
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("count", count);
        return Result.success(data);
    }

    @ApiOperation("标记已读")
    @PostMapping("/{id}/read")
    public Result<?> markRead(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        notificationService.markRead(id, userId);
        return Result.success();
    }

    @ApiOperation("全部已读")
    @PostMapping("/read-all")
    public Result<?> markAllRead() {
        Long userId = UserContext.getUserId();
        notificationService.markAllRead(userId);
        return Result.success();
    }

    @ApiOperation("删除通知")
    @DeleteMapping("/{id}")
    public Result<?> deleteNotification(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        notificationService.deleteNotification(id, userId);
        return Result.success();
    }

    /**
     * 管理员手动发送通知（用于系统公告等）
     */
    @ApiOperation("管理员发送通知")
    @PostMapping("/send")
    @RequireRole({"admin"})
    public Result<?> send(@RequestBody Map<String, Object> body) {
        Long targetUserId = Long.valueOf(body.get("userId").toString());
        String title = (String) body.get("title");
        String content = (String) body.getOrDefault("content", "");
        String type = (String) body.getOrDefault("type", "SYSTEM");
        notificationService.sendNotification(targetUserId, title, content, type, null, null);
        return Result.success();
    }
}
