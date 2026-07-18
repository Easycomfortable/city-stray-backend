package com.citystray.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayProperties {

    /** 小程序 appId */
    private String appid;

    /** 商户号 */
    private String mchId;

    /** API 密钥 */
    private String apiKey;

    /** 支付结果回调地址 */
    private String notifyUrl;
}
