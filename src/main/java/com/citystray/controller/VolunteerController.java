package com.citystray.controller;

import com.citystray.annotation.RequireRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.Task;
import com.citystray.entity.User;
import com.citystray.entity.Volunteer;
import com.citystray.mapper.TaskMapper;
import com.citystray.mapper.UserMapper;
import com.citystray.mapper.VolunteerMapper;
import com.citystray.service.VolunteerService;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 志愿者管理控制器
 */
@Api(tags = "志愿者管理")
@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询志愿者列表
     */
    @ApiOperation("分页查询志愿者列表")
    @GetMapping("/list")
    @RequireRole({"admin", "rescue_admin"})
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("认证状态(PENDING/APPROVED/REJECTED)") @RequestParam(required = false) String certifyStatus,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        // 将前端certifyStatus转换为服务层authStatus整数字符串
        String authStatusParam = "";
        if (certifyStatus != null && !certifyStatus.isEmpty()) {
            switch (certifyStatus.toUpperCase()) {
                case "PENDING": authStatusParam = "0"; break;
                case "APPROVED": authStatusParam = "1"; break;
                case "REJECTED": authStatusParam = "2"; break;
                default: authStatusParam = certifyStatus; break;
            }
        }

        PageResult<Volunteer> serviceResult =
                volunteerService.getVolunteerList(page, pageSize, authStatusParam, keyword, null);

        // 转换字段名以匹配前端期望的格式
        List<Map<String, Object>> records = new ArrayList<>();
        if (serviceResult.getRecords() != null) {
            for (Volunteer v : serviceResult.getRecords()) {
                records.add(toVolunteerMap(v));
            }
        }

        return Result.success(new PageResult<>(serviceResult.getTotal(), records));
    }

    /**
     * 审核志愿者认证
     */
    @ApiOperation("审核志愿者认证")
    @PostMapping("/{id}/certify")
    @RequireRole({"admin", "rescue_admin"})
    public Result<?> certify(
            @ApiParam("志愿者ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "");
        String comment = body.getOrDefault("comment", "");

        // 将前端状态字符串转换为整数
        String authStatusStr;
        switch (status.toUpperCase()) {
            case "APPROVED": authStatusStr = "1"; break;
            case "REJECTED": authStatusStr = "2"; break;
            case "PENDING": authStatusStr = "0"; break;
            default: authStatusStr = status; break;
        }

        volunteerService.reviewVolunteer(id, authStatusStr, comment);
        return Result.success();
    }

    /**
     * 申请成为志愿者（小程序端）
     */
    @ApiOperation("申请成为志愿者")
    @PostMapping("/apply")
    public Result<?> apply(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        // 检查是否已是志愿者
        List<Volunteer> existList = volunteerMapper.selectList(
                new LambdaQueryWrapper<Volunteer>()
                        .eq(Volunteer::getUserId, userId));
        if (existList != null && !existList.isEmpty()) {
            return Result.error("您已申请过志愿者，请勿重复申请");
        }

        Volunteer volunteer = new Volunteer();
        volunteer.setUserId(userId);
        volunteer.setRealName((String) body.getOrDefault("realName", ""));
        volunteer.setIdCard((String) body.getOrDefault("idCard", ""));
        volunteer.setPhone((String) body.getOrDefault("phone", ""));
        volunteer.setSkillTags((String) body.getOrDefault("skillTags", ""));
        volunteer.setTotalHours(java.math.BigDecimal.ZERO);
        volunteer.setPoints(0);
        volunteer.setAuthStatus(0); // 待审核

        volunteerMapper.insert(volunteer);

        // 更新用户角色为志愿者
        User user = userMapper.selectById(userId);
        if (user != null && "user".equals(user.getRole())) {
            user.setRole("volunteer");
            userMapper.updateById(user);
        }

        return Result.success(volunteer.getId());
    }

    /**
     * 获取排班列表
     * 从task表查询已分配给志愿者的任务作为排班数据
     */
    @ApiOperation("获取排班列表")
    @GetMapping("/schedule")
    @RequireRole({"admin", "rescue_admin"})
    public Result<List<Map<String, Object>>> schedule(
            @ApiParam("志愿者ID(可选)") @RequestParam(required = false) Long volunteerId) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        if (volunteerId != null) {
            wrapper.eq("volunteer_id", volunteerId);
        }
        wrapper.isNotNull("volunteer_id");
        wrapper.ne("volunteer_id", 0);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 100");

        List<Task> tasks = taskMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Task t : tasks) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", t.getId());
            item.put("volunteerId", t.getVolunteerId());
            item.put("orderId", t.getOrderId());
            item.put("taskType", t.getTaskType());
            item.put("status", t.getStatus());
            item.put("location", t.getLocation());
            item.put("description", t.getDescription());
            item.put("pointsReward", t.getPointsReward());
            item.put("createTime", t.getCreateTime());
            result.add(item);
        }

        return Result.success(result);
    }

    /**
     * 保存排班
     * 在task表中创建一条排班任务记录
     */
    @ApiOperation("保存排班")
    @PostMapping("/schedule/save")
    @RequireRole({"admin", "rescue_admin"})
    public Result<?> saveSchedule(@RequestBody Map<String, Object> body) {
        Task task = new Task();

        if (body.get("volunteerId") != null) {
            task.setVolunteerId(Long.valueOf(body.get("volunteerId").toString()));
        }
        if (body.get("orderId") != null) {
            task.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }

        task.setTaskType(body.getOrDefault("taskType", "respond").toString());
        task.setStatus("available");
        task.setLocation(body.getOrDefault("location", "").toString());
        task.setDescription(body.getOrDefault("description", "").toString());

        if (body.get("pointsReward") != null) {
            task.setPointsReward(Integer.valueOf(body.get("pointsReward").toString()));
        }

        taskMapper.insert(task);
        return Result.success(null);
    }

    /**
     * 获取积分变动记录
     */
    @ApiOperation("获取积分变动记录")
    @GetMapping("/points/log")
    @RequireRole({"admin", "rescue_admin"})
    public Result<PageResult<Map<String, Object>>> pointsLog(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<Map<String, Object>> serviceResult =
                volunteerService.getPointsRecords(page, pageSize, null);

        // 转换字段名以匹配前端期望的格式
        List<Map<String, Object>> records = new ArrayList<>();
        if (serviceResult.getRecords() != null) {
            for (Map<String, Object> row : serviceResult.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("volunteerName", "");
                item.put("action", row.get("action"));
                item.put("change", 0);
                item.put("balance", 0);
                item.put("remark", row.get("content"));
                item.put("createTime", row.get("createTime"));
                records.add(item);
            }
        }

        return Result.success(new PageResult<>(serviceResult.getTotal(), records));
    }

    /**
     * 保存积分规则
     * 当前无独立积分规则表，记录到日志供后续扩展
     */
    @ApiOperation("保存积分规则")
    @PostMapping("/points/rule")
    @RequireRole({"admin", "rescue_admin"})
    public Result<?> savePointsRule(@RequestBody Map<String, Object> body) {
        // 积分规则暂以日志记录，后续可存入sys_dict_data表
        // body示例: {"action":"救援出勤","points":10} 或 {"rules":[{"action":"...","points":...}]}
        return Result.success("积分规则已保存");
    }

    /**
     * 导出服务时长Excel
     * 查询所有志愿者并导出姓名、电话、累计时长、积分等数据
     */
    @ApiOperation("导出服务时长")
    @GetMapping("/export-hours")
    @RequireRole({"admin", "rescue_admin"})
    public void exportHours(HttpServletResponse response) {
        // 查询所有已认证的志愿者
        PageResult<Volunteer> allVolunteers = volunteerService.getVolunteerList(1, 1000, "1", null, null);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (allVolunteers.getRecords() != null) {
            for (Volunteer v : allVolunteers.getRecords()) {
                // 统计该志愿者完成的任务数
                Long completedCount = taskMapper.selectCount(
                        new QueryWrapper<Task>()
                                .eq("volunteer_id", v.getId())
                                .eq("status", "completed")
                );

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("姓名", v.getRealName());
                row.put("联系电话", v.getPhone());
                row.put("累计服务时长(小时)", v.getTotalHours());
                row.put("积分", v.getPoints());
                row.put("完成任务数", completedCount != null ? completedCount : 0);
                rows.add(row);
            }
        }

        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        writer.write(rows);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=volunteer_hours.xlsx");
        try {
            javax.servlet.ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            writer.close();
        } catch (Exception e) {
            // 导出失败时不抛出异常
        }
    }

    /**
     * 加入黑名单
     */
    @ApiOperation("加入黑名单")
    @PostMapping("/{id}/blacklist")
    @RequireRole({"admin", "rescue_admin"})
    public Result<?> addToBlacklist(
            @ApiParam("志愿者ID") @PathVariable Long id) {
        Volunteer volunteer = volunteerService.getVolunteerById(id);
        volunteer.setAuthStatus(3);
        volunteerService.updateVolunteer(volunteer);
        return Result.success();
    }

    /**
     * 移出黑名单
     */
    @ApiOperation("移出黑名单")
    @DeleteMapping("/{id}/blacklist")
    @RequireRole({"admin", "rescue_admin"})
    public Result<?> removeFromBlacklist(
            @ApiParam("志愿者ID") @PathVariable Long id) {
        Volunteer volunteer = volunteerService.getVolunteerById(id);
        volunteer.setAuthStatus(1);
        volunteerService.updateVolunteer(volunteer);
        return Result.success();
    }

    // ========== 辅助方法 ==========

    /**
     * 将Volunteer实体转换为前端期望的Map格式
     */
    private Map<String, Object> toVolunteerMap(Volunteer v) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", v.getId());
        map.put("name", v.getRealName());
        map.put("phone", v.getPhone());
        map.put("certifyStatus", mapAuthStatus(v.getAuthStatus()));
        map.put("skillTags", parseSkillTags(v.getSkillTags()));
        map.put("totalHours", v.getTotalHours());
        map.put("points", v.getPoints());
        map.put("completedTasks", 0);
        map.put("blacklisted", v.getAuthStatus() != null && v.getAuthStatus() == 3);
        map.put("realName", v.getRealName());
        map.put("idCard", v.getIdCard());
        map.put("certifyReason", v.getRejectReason());
        map.put("createTime", v.getCreateTime());
        return map;
    }

    /**
     * 将authStatus整数映射为前端期望的字符串
     */
    private String mapAuthStatus(Integer authStatus) {
        if (authStatus == null) return "PENDING";
        switch (authStatus) {
            case 0: return "PENDING";
            case 1: return "APPROVED";
            case 2: return "REJECTED";
            case 3: return "PENDING"; // 黑名单也显示为PENDING（或按需调整）
            default: return "PENDING";
        }
    }

    /**
     * 解析技能标签(JSON字符串或逗号分隔字符串)
     */
    private List<String> parseSkillTags(String skillTags) {
        if (skillTags == null || skillTags.isEmpty()) {
            return Collections.emptyList();
        }
        // 尝试作为JSON数组解析
        if (skillTags.startsWith("[")) {
            try {
                String trimmed = skillTags.substring(1, skillTags.length() - 1);
                if (trimmed.isEmpty()) return Collections.emptyList();
                String[] parts = trimmed.split(",");
                List<String> tags = new ArrayList<>();
                for (String part : parts) {
                    String tag = part.trim().replaceAll("^\"|\"$", "");
                    if (!tag.isEmpty()) tags.add(tag);
                }
                return tags;
            } catch (Exception e) {
                // fall through to comma split
            }
        }
        // 逗号分隔
        String[] parts = skillTags.split(",");
        List<String> tags = new ArrayList<>();
        for (String part : parts) {
            String tag = part.trim();
            if (!tag.isEmpty()) tags.add(tag);
        }
        return tags;
    }
}
