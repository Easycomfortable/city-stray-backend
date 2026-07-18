package com.citystray.controller;

import com.citystray.common.Result;
import com.citystray.service.WxMaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信小程序登录控制器
 */
@Api(tags = "微信小程序")
@RestController
@RequestMapping("/api/wx")
@RequiredArgsConstructor
@Slf4j
public class WxMaController {

    private final WxMaService wxMaService;

    @ApiOperation("微信小程序登录")
    @PostMapping("/login")
    public Result<?> wxLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }
        Map<String, Object> data = wxMaService.wxLogin(code);
        return Result.success(data);
    }
}
