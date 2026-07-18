package com.citystray.service;

public interface WxMessageService {

    /**
     * 发送微信订阅消息
     * 根据用户ID查找openid，构建模板数据并调用微信API发送
     *
     * @param userId      接收用户ID
     * @param title       通知标题（用于模板thing字段）
     * @param content     通知内容
     * @param type        通知类型
     * @param relatedType 关联业务类型
     * @param relatedId   关联业务ID
     */
    void sendSubscribeMessage(Long userId, String title, String content,
                               String type, String relatedType, Long relatedId);
}
