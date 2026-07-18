package com.citystray.service;

import java.math.BigDecimal;
import java.util.Map;

public interface WxPayService {

    /**
     * 创建微信预付单（统一下单）
     *
     * @param donationRecordId 捐赠记录ID
     * @param amount           支付金额（元）
     * @param description      商品描述
     * @param openid           支付用户的openid
     * @return 小程序调起支付所需的参数（timeStamp, nonceStr, package, signType, paySign）
     */
    Map<String, String> createPrepayOrder(Long donationRecordId, BigDecimal amount,
                                           String description, String openid);

    /**
     * 处理微信支付回调通知
     *
     * @param xmlData 微信回调的 XML 数据
     * @return 返回给微信的 XML 响应
     */
    String handlePayNotify(String xmlData);
}
