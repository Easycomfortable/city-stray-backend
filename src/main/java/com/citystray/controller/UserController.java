package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.SysLog;
import com.citystray.entity.User;
import com.citystray.entity.StrayReport;
import com.citystray.entity.AdoptionApply;
import com.citystray.entity.Volunteer;
import com.citystray.mapper.UserMapper;
import com.citystray.mapper.StrayReportMapper;
import com.citystray.mapper.AdoptionApplyMapper;
import com.citystray.mapper.VolunteerMapper;
import com.citystray.service.LogService;
import com.citystray.annotation.OperationLog;
import com.citystray.service.UserService;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户管理")
public class UserController {

    private final UserService userService;
    private final LogService logService;
    private final UserMapper userMapper;
    private final StrayReportMapper strayReportMapper;
    private final AdoptionApplyMapper adoptionApplyMapper;
    private final VolunteerMapper volunteerMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @OperationLog(module = "用户管理", type = "LOGIN", content = "用户登录")
    @ApiOperation("用户登录")
    public Result<?> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        Map<String, Object> data = userService.login(username, password);

        // 记录登录日志
        Long userId = data.get("id") != null ? Long.valueOf(data.get("id").toString()) : null;
        SysLog sysLog = new SysLog();
        sysLog.setUserId(userId);
        sysLog.setUsername(username);
        sysLog.setModule("用户管理");
        sysLog.setType("LOGIN");
        sysLog.setContent("用户登录成功");
        sysLog.setSuccess(1);
        logService.save(sysLog);

        return Result.success(data);
    }

    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    public Result<?> getUserInfo() {
        Map<String, Object> data = userService.getUserInfo();
        return Result.success(data);
    }

    @GetMapping("/my-stats")
    @ApiOperation("获取当前用户统计数据(上报数/领养数/积分)")
    public Result<?> myStats() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.success(Map.of("reportCount", 0, "adoptCount", 0, "points", 0));
        }

        // 上报数量
        long reportCount = strayReportMapper.selectCount(
                new LambdaQueryWrapper<StrayReport>().eq(StrayReport::getUserId, userId));

        // 领养申请数量
        long adoptCount = adoptionApplyMapper.selectCount(
                new LambdaQueryWrapper<AdoptionApply>().eq(AdoptionApply::getUserId, userId));

        // 积分(从志愿者表查)
        int points = 0;
        Volunteer volunteer = volunteerMapper.selectOne(
                new LambdaQueryWrapper<Volunteer>().eq(Volunteer::getUserId, userId));
        if (volunteer != null && volunteer.getPoints() != null) {
            points = volunteer.getPoints();
        }

        return Result.success(Map.of(
                "reportCount", reportCount,
                "adoptCount", adoptCount,
                "points", points
        ));
    }

    @PostMapping("/logout")
    @OperationLog(module = "用户管理", type = "LOGIN", content = "用户退出登录")
    @ApiOperation("用户退出登录")
    public Result<?> logout() {
        userService.logout();
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("分页查询用户列表")
    public Result<?> list(
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("角色") @RequestParam(required = false) String role,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<User> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword)
            );
        }

        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }

        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);

        PageResult<User> pageResult = new PageResult<>(userPage.getTotal(), userPage.getRecords());
        return Result.success(pageResult);
    }

    @PutMapping("/{id}/status")
    @OperationLog(module = "用户管理", type = "UPDATE", content = "更新用户状态")
    @ApiOperation("更新用户状态")
    public Result<?> updateStatus(
            @ApiParam("用户ID") @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {

        Integer status = body.get("status");
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);

        return Result.success();
    }

    @PutMapping("/profile")
    @OperationLog(module = "用户管理", type = "UPDATE", content = "修改个人信息")
    @ApiOperation("修改个人信息")
    public Result<?> updateProfile(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 只允许修改昵称、手机号、头像
        if (StringUtils.hasText(body.get("nickname"))) {
            user.setNickname(body.get("nickname"));
        }
        if (body.containsKey("phone")) {
            user.setPhone(body.get("phone"));
        }
        if (body.containsKey("avatar")) {
            user.setAvatar(body.get("avatar"));
        }

        userMapper.updateById(user);
        return Result.success();
    }

    @PutMapping("/password")
    @OperationLog(module = "用户管理", type = "UPDATE", content = "修改密码")
    @ApiOperation("修改密码")
    public Result<?> updatePassword(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return Result.error("旧密码和新密码不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("旧密码不正确");
        }

        // 更新为新密码（BCrypt加密）
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success();
    }
}
