package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.entity.SysNotification;
import com.citystray.mapper.SysNotificationMapper;
import com.citystray.service.NotificationService;
import com.citystray.service.WxMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SysNotificationMapper notificationMapper;
    private final WxMessageService wxMessageService;

    @Override
    public void sendNotification(Long userId, String title, String content,
                                  String type, String relatedType, Long relatedId) {
        if (userId == null) {
            log.warn("通知发送失败: userId为空, title={}", title);
            return;
        }

        // 1. 写入站内通知
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);
        notificationMapper.insert(notification);

        log.info("站内通知已发送: userId={}, title={}", userId, title);

        // 2. 异步推送微信订阅消息（不阻塞主流程）
        try {
            wxMessageService.sendSubscribeMessage(userId, title, content, type, relatedType, relatedId);
        } catch (Exception e) {
            log.warn("微信订阅消息推送失败(不影响站内通知): {}", e.getMessage());
        }
    }

    @Override
    public PageResult<SysNotification> getNotificationList(Long userId, Integer page,
                                                            Integer pageSize, Boolean unreadOnly, String type) {
        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotification::getUserId, userId);
        if (Boolean.TRUE.equals(unreadOnly)) {
            wrapper.eq(SysNotification::getIsRead, false);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(SysNotification::getType, type);
        }
        wrapper.orderByDesc(SysNotification::getCreateTime);
        Page<SysNotification> pageObj = notificationMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return new PageResult<>(pageObj.getTotal(), pageObj.getRecords());
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public void markRead(Long id, Long userId) {
        SysNotification notification = notificationMapper.selectById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(true);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllRead(Long userId) {
        notificationMapper.markAllRead(userId);
    }

    @Override
    public void deleteNotification(Long id, Long userId) {
        SysNotification notification = notificationMapper.selectById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            notificationMapper.deleteById(id);
        }
    }
}
