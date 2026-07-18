package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citystray.common.Result;
import com.citystray.entity.DonationRecord;
import com.citystray.entity.User;
import com.citystray.mapper.DonationRecordMapper;
import com.citystray.mapper.UserMapper;
import com.citystray.service.WxPayService;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信支付控制器
 */
@Api(tags = "微信支付")
@RestController
@RequestMapping("/api/wx/pay")
@RequiredArgsConstructor
@Slf4j
public class WxPayController {

    private final WxPayService wxPayService;
    private final DonationRecordMapper donationRecordMapper;
    private final UserMapper userMapper;

    @ApiOperation("创建微信预付单")
    @PostMapping("/create")
    public Result<?> createOrder(@RequestBody Map<String, Object> body) {
        Long donationRecordId = Long.valueOf(body.get("donationRecordId").toString());
        String description = body.getOrDefault("description", "城流浪捐赠").toString();

        // 获取当前用户的 openid
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getOpenid() == null) {
            return Result.error("用户未绑定微信，无法支付");
        }

        // 查询捐赠记录获取金额
        DonationRecord record = donationRecordMapper.selectById(donationRecordId);
        if (record == null) {
            return Result.error("捐赠记录不存在");
        }

        Map<String, String> payParams = wxPayService.createPrepayOrder(
                donationRecordId, record.getAmount(), description, user.getOpenid());

        return Result.success(payParams);
    }

    @ApiOperation("微信支付回调通知")
    @PostMapping("/notify")
    public String payNotify(@RequestBody String xmlData) {
        return wxPayService.handlePayNotify(xmlData);
    }
}
