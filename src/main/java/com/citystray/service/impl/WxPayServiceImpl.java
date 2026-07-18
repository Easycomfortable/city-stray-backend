package com.citystray.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.config.WxPayProperties;
import com.citystray.entity.DonationProject;
import com.citystray.entity.DonationRecord;
import com.citystray.mapper.DonationProjectMapper;
import com.citystray.mapper.DonationRecordMapper;
import com.citystray.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    private final WxPayProperties wxPayProperties;
    private final DonationRecordMapper donationRecordMapper;
    private final DonationProjectMapper donationProjectMapper;
    private final com.citystray.service.NotificationService notificationService;

    /** 微信支付统一下单接口 */
    private static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Override
    public Map<String, String> createPrepayOrder(Long donationRecordId, BigDecimal amount,
                                                  String description, String openid) {
        // 生成商户订单号
        String outTradeNo = IdUtil.getSnowflakeNextIdStr();

        // 更新捐赠记录的支付流水号
        DonationRecord record = new DonationRecord();
        record.setId(donationRecordId);
        record.setPaymentNo(outTradeNo);
        record.setPaymentMethod("WECHAT");
        record.setStatus("PENDING");
        donationRecordMapper.updateById(record);

        // 金额转为分
        int totalFee = amount.multiply(new BigDecimal("100")).intValue();

        // 构建请求参数
        SortedMap<String, String> params = new TreeMap<>();
        params.put("appid", wxPayProperties.getAppid());
        params.put("mch_id", wxPayProperties.getMchId());
        params.put("nonce_str", IdUtil.fastSimpleUUID());
        params.put("body", description);
        params.put("out_trade_no", outTradeNo);
        params.put("total_fee", String.valueOf(totalFee));
        params.put("spbill_create_ip", getClientIp());
        params.put("notify_url", wxPayProperties.getNotifyUrl());
        params.put("trade_type", "JSAPI");
        params.put("openid", openid);

        // 生成签名
        String sign = generateSign(params);
        params.put("sign", sign);

        // 转换为 XML
        String requestXml = mapToXml(params);
        log.info("微信统一下单请求: {}", requestXml);

        // 发送请求
        String responseXml = HttpUtil.post(UNIFIED_ORDER_URL, requestXml);
        log.info("微信统一下单响应: {}", responseXml);

        // 解析响应
        Map<String, String> responseMap = parseXml(responseXml);

        if (!"SUCCESS".equals(responseMap.get("return_code"))) {
            throw new RuntimeException("微信支付下单失败: " + responseMap.get("return_msg"));
        }
        if (!"SUCCESS".equals(responseMap.get("result_code"))) {
            throw new RuntimeException("微信支付下单失败: " + responseMap.get("err_code_des"));
        }

        String prepayId = responseMap.get("prepay_id");

        // 构建小程序调起支付所需的参数
        Map<String, String> payParams = new LinkedHashMap<>();
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = IdUtil.fastSimpleUUID();
        String packageVal = "prepay_id=" + prepayId;

        payParams.put("timeStamp", timeStamp);
        payParams.put("nonceStr", nonceStr);
        payParams.put("package", packageVal);
        payParams.put("signType", "MD5");

        // 生成支付签名
        SortedMap<String, String> signParams = new TreeMap<>();
        signParams.put("appId", wxPayProperties.getAppid());
        signParams.put("timeStamp", timeStamp);
        signParams.put("nonceStr", nonceStr);
        signParams.put("package", packageVal);
        signParams.put("signType", "MD5");
        payParams.put("paySign", generateSign(signParams));

        return payParams;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handlePayNotify(String xmlData) {
        log.info("微信支付回调通知: {}", xmlData);

        Map<String, String> notifyMap = parseXml(xmlData);

        // 验证签名
        String sign = notifyMap.get("sign");
        notifyMap.remove("sign");
        String expectedSign = generateSign(new TreeMap<>(notifyMap));

        if (!expectedSign.equals(sign)) {
            log.error("微信支付回调签名验证失败");
            return buildResponseXml("FAIL", "签名验证失败");
        }

        String returnCode = notifyMap.get("return_code");
        String resultCode = notifyMap.get("result_code");

        if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
            String outTradeNo = notifyMap.get("out_trade_no");
            String transactionId = notifyMap.get("transaction_id");

            // 查询捐赠记录
            LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DonationRecord::getPaymentNo, outTradeNo);
            DonationRecord record = donationRecordMapper.selectOne(wrapper);

            if (record != null && "PENDING".equals(record.getStatus())) {
                // 更新支付状态
                record.setStatus("SUCCESS");
                record.setTransactionId(transactionId);
                record.setTransactionTime(LocalDateTime.now());
                donationRecordMapper.updateById(record);

                // 更新项目已筹金额和捐赠人数
                if (record.getProjectId() != null) {
                    donationProjectMapper.addDonation(record.getProjectId(), record.getAmount());
                }

                // 通知捐赠人支付成功
                if (record.getUserId() != null) {
                    notificationService.sendNotification(
                        record.getUserId(),
                        "捐赠成功",
                        "您的爱心捐赠 ¥" + record.getAmount() + " 已支付成功，感谢您的善举！",
                        "DONATION", "DONATION", record.getId()
                    );
                }

                log.info("微信支付成功, 订单号: {}, 交易号: {}", outTradeNo, transactionId);
            }
        }

        return buildResponseXml("SUCCESS", "OK");
    }

    /**
     * 生成微信支付签名（MD5）
     */
    private String generateSign(SortedMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StrUtil.isNotBlank(entry.getValue())) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.append("key=").append(wxPayProperties.getApiKey());
        return SecureUtil.md5(sb.toString()).toUpperCase();
    }

    /**
     * Map 转 XML
     */
    private String mapToXml(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append(entry.getValue());
            sb.append("</").append(entry.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 解析微信回调 XML
     */
    private Map<String, String> parseXml(String xml) {
        Map<String, String> map = new HashMap<>();
        try {
            // 简单的 XML 解析（适用于微信返回的扁平结构）
            xml = xml.replace("<xml>", "").replace("</xml>", "");
            String[] items = xml.split("</[^>]+>");
            for (String item : items) {
                int start = item.indexOf(">");
                if (start > 0) {
                    String key = item.substring(1, start);
                    String value = item.substring(start + 1);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("XML解析失败: {}", xml, e);
        }
        return map;
    }

    /**
     * 构建返回给微信的 XML 响应
     */
    private String buildResponseXml(String returnCode, String returnMsg) {
        return "<xml><return_code><![CDATA[" + returnCode
                + "]]></return_code><return_msg><![CDATA[" + returnMsg
                + "]]></return_msg></xml>";
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
