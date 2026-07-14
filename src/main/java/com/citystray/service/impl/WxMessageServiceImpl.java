package com.citystray.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.config.WxMaProperties;
import com.citystray.entity.User;
import com.citystray.entity.WxSubscribeRecord;
import com.citystray.mapper.UserMapper;
import com.citystray.mapper.WxSubscribeRecordMapper;
import com.citystray.service.WxMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxMessageServiceImpl implements WxMessageService {

    private final WxMaProperties wxMaProperties;
    private final UserMapper userMapper;
    private final WxSubscribeRecordMapper recordMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String SEND_MSG_URL =
            "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={token}";

    /**
     * 各业务类型对应的模板ID（需在微信小程序后台申请后替换）
     */
    private static final String TEMPLATE_ADOPTION = "YOUR_ADOPTION_TEMPLATE_ID";
    private static final String TEMPLATE_RESCUE   = "YOUR_RESCUE_TEMPLATE_ID";
    private static final String TEMPLATE_STORY    = "YOUR_STORY_TEMPLATE_ID";
    private static final String TEMPLATE_DONATION = "YOUR_DONATION_TEMPLATE_ID";
    private static final String TEMPLATE_SYSTEM   = "YOUR_SYSTEM_TEMPLATE_ID";

    @Override
    @Async
    public void sendSubscribeMessage(Long userId, String title, String content,
                                      String type, String relatedType, Long relatedId) {
        // 1. 查找用户openid
        User user = userMapper.selectById(userId);
        if (user == null || user.getOpenid() == null || user.getOpenid().isEmpty()) {
            log.debug("跳过微信推送: 用户{}未绑定openid", userId);
            return;
        }

        // 2. 选择模板ID
        String templateId = getTemplateId(type);
        if (templateId.startsWith("YOUR_")) {
            log.debug("跳过微信推送: 模板ID未配置(type={})", type);
            saveRecord(userId, user.getOpenid(), templateId, title, "SKIPPED", "模板ID未配置");
            return;
        }

        // 3. 获取 access_token
        String accessToken = getAccessToken();
        if (accessToken == null) {
            log.error("微信推送失败: 无法获取access_token");
            saveRecord(userId, user.getOpenid(), templateId, title, "FAILED", "无法获取access_token");
            return;
        }

        // 4. 构建消息体
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing1", truncMap(title, 20));
        data.put("thing2", truncMap(content, 20));
        data.put("time3", truncMap(LocalDateTime.now().toString().replace("T", " "), 20));

        Map<String, Object> msgBody = new LinkedHashMap<>();
        msgBody.put("touser", user.getOpenid());
        msgBody.put("template_id", templateId);
        msgBody.put("page", buildPage(relatedType, relatedId));
        msgBody.put("data", data);

        String requestBody = JSONUtil.toJsonStr(msgBody);
        String url = SEND_MSG_URL.replace("{token}", accessToken);

        try {
            String response = HttpUtil.post(url, requestBody);
            JSONObject respJson = JSONUtil.parseObj(response);
            int errcode = respJson.getInt("errcode", -1);

            if (errcode == 0) {
                log.info("微信订阅消息发送成功: userId={}, templateId={}", userId, templateId);
                saveRecord(userId, user.getOpenid(), templateId, title, "SENT", "");
            } else {
                String errmsg = respJson.getStr("errmsg", "未知错误");
                log.warn("微信订阅消息发送失败: errcode={}, errmsg={}", errcode, errmsg);
                saveRecord(userId, user.getOpenid(), templateId, title, "FAILED", errmsg);
            }
        } catch (Exception e) {
            log.error("微信订阅消息发送异常: {}", e.getMessage());
            saveRecord(userId, user.getOpenid(), templateId, title, "FAILED", e.getMessage());
        }
    }

    /**
     * 根据类型获取模板ID
     */
    private String getTemplateId(String type) {
        switch (type != null ? type : "") {
            case "ADOPTION":  return TEMPLATE_ADOPTION;
            case "RESCUE":    return TEMPLATE_RESCUE;
            case "STORY":     return TEMPLATE_STORY;
            case "DONATION":  return TEMPLATE_DONATION;
            default:          return TEMPLATE_SYSTEM;
        }
    }

    /**
     * 构建跳转页面
     */
    private String buildPage(String relatedType, Long relatedId) {
        if (relatedType == null || relatedId == null) {
            return "pages/index/index";
        }
        switch (relatedType) {
            case "ADOPTION":  return "pages/adoption/detail?id=" + relatedId;
            case "RESCUE":    return "pages/rescue/detail?id=" + relatedId;
            case "DONATION":  return "pages/donation/donate";
            default:          return "pages/notification/list";
        }
    }

    /**
     * 获取小程序 access_token（Redis缓存，有效期2小时）
     */
    private String getAccessToken() {
        String cacheKey = "wx:access_token";
        String token = stringRedisTemplate.opsForValue().get(cacheKey);
        if (token != null && !token.isEmpty()) {
            return token;
        }

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                + "&appid=" + wxMaProperties.getAppid()
                + "&secret=" + wxMaProperties.getSecret();

        try {
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);
            token = json.getStr("access_token");
            if (token != null && !token.isEmpty()) {
                // 缓存7000秒（官方7200秒，提前200秒刷新）
                stringRedisTemplate.opsForValue().set(cacheKey, token,
                        java.time.Duration.ofSeconds(7000));
                return token;
            }
            log.error("获取access_token失败: {}", response);
        } catch (Exception e) {
            log.error("获取access_token异常: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 微信模板数据格式: {value: "xxx"}
     */
    private Map<String, String> truncMap(String val, int maxLen) {
        String v = val != null ? val : "";
        if (v.length() > maxLen) v = v.substring(0, maxLen - 1) + "...";
        Map<String, String> m = new LinkedHashMap<>();
        m.put("value", v);
        return m;
    }

    /**
     * 保存发送记录
     */
    private void saveRecord(Long userId, String openid, String templateId,
                             String title, String status, String errorMsg) {
        WxSubscribeRecord record = new WxSubscribeRecord();
        record.setUserId(userId);
        record.setOpenid(openid);
        record.setTemplateId(templateId);
        record.setDataJson(title);
        record.setStatus(status);
        record.setErrorMsg(errorMsg);
        if ("SENT".equals(status)) {
            record.setSendTime(LocalDateTime.now());
        }
        recordMapper.insert(record);
    }
}
