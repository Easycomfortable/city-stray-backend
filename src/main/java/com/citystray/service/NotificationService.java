package com.citystray.service;

import com.citystray.common.PageResult;
import com.citystray.entity.SysNotification;

public interface NotificationService {

    /**
     * 发送站内通知（同时尝试推送微信订阅消息）
     */
    void sendNotification(Long userId, String title, String content,
                          String type, String relatedType, Long relatedId);

    /**
     * 获取用户通知列表（分页）
     */
    PageResult<SysNotification> getNotificationList(Long userId, Integer page,
                                                     Integer pageSize, Boolean unreadOnly);

    /**
     * 获取未读数量
     */
    int getUnreadCount(Long userId);

    /**
     * 标记单条已读
     */
    void markRead(Long id, Long userId);

    /**
     * 标记全部已读
     */
    void markAllRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long id, Long userId);
}
