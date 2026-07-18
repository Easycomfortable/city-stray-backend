package com.citystray.service;

import java.util.Map;

public interface WxMaService {

    /**
     * 微信小程序登录
     * 通过 wx.login() 获取的 code 换取 openid 和 session_key，
     * 然后查找或创建用户，返回 JWT token 和用户信息
     *
     * @param code 小程序 wx.login() 获取的临时登录凭证
     * @return 包含 token、用户信息的 Map
     */
    Map<String, Object> wxLogin(String code);
}
