package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.Task;
import com.citystray.entity.User;
import com.citystray.entity.Volunteer;
import com.citystray.entity.VolunteerSchedule;
import com.citystray.entity.ExchangeRequest;
import com.citystray.entity.PointsRule;
import com.citystray.entity.TaskLog;
import com.citystray.mapper.TaskMapper;
import com.citystray.mapper.UserMapper;
import com.citystray.mapper.VolunteerMapper;
import com.citystray.mapper.VolunteerScheduleMapper;
import com.citystray.mapper.ExchangeRequestMapper;
import com.citystray.mapper.PointsRuleMapper;
import com.citystray.mapper.TaskLogMapper;
import com.citystray.service.VolunteerService;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
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
    private VolunteerScheduleMapper scheduleMapper;

    @Autowired
    private ExchangeRequestMapper exchangeRequestMapper;

    @Autowired
    private PointsRuleMapper pointsRuleMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询志愿者列表
     */
    @ApiOperation("分页查询志愿者列表")
    @GetMapping("/list")
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
                case "BLACKLISTED": authStatusParam = "3"; break;
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
     * 获取排班列表（按月查询）
     */
    @ApiOperation("获取排班列表")
    @GetMapping("/schedule")
    public Result<List<Map<String, Object>>> schedule(
            @ApiParam("年份") @RequestParam(defaultValue = "2026") Integer year,
            @ApiParam("月份") @RequestParam(defaultValue = "1") Integer month,
            @ApiParam("志愿者ID(可选)") @RequestParam(required = false) Long volunteerId) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LambdaQueryWrapper<VolunteerSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(VolunteerSchedule::getScheduleDate, startDate);
        wrapper.le(VolunteerSchedule::getScheduleDate, endDate);
        if (volunteerId != null) {
            wrapper.eq(VolunteerSchedule::getVolunteerId, volunteerId);
        }
        wrapper.orderByAsc(VolunteerSchedule::getScheduleDate);

        List<VolunteerSchedule> schedules = scheduleMapper.selectList(wrapper);

        // 预加载志愿者名称
        Map<Long, String> volunteerNames = new HashMap<>();
        for (VolunteerSchedule s : schedules) {
            if (!volunteerNames.containsKey(s.getVolunteerId())) {
                Volunteer v = volunteerMapper.selectById(s.getVolunteerId());
                volunteerNames.put(s.getVolunteerId(), v != null ? v.getRealName() : "未知");
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (VolunteerSchedule s : schedules) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", s.getId());
            item.put("volunteerId", s.getVolunteerId());
            item.put("volunteerName", volunteerNames.getOrDefault(s.getVolunteerId(), "未知"));
            item.put("date", s.getScheduleDate() != null ? s.getScheduleDate().toString() : "");
            item.put("shift", s.getShiftType());
            item.put("region", s.getRegion());
            item.put("remark", s.getRemark());
            result.add(item);
        }

        return Result.success(result);
    }

    /**
     * 保存排班（新增或更新）
     */
    @ApiOperation("保存排班")
    @PostMapping("/schedule/save")
    public Result<?> saveSchedule(@RequestBody Map<String, Object> body) {
        Long id = body.get("id") != null ? Long.valueOf(body.get("id").toString()) : null;
        Long volunteerId = body.get("volunteerId") != null ? Long.valueOf(body.get("volunteerId").toString()) : null;
        String date = body.getOrDefault("date", "").toString();
        String shift = body.getOrDefault("shift", "").toString();
        String region = body.getOrDefault("region", "").toString();
        String remark = body.getOrDefault("remark", "").toString();

        if (volunteerId == null || date.isEmpty() || shift.isEmpty()) {
            return Result.error("志愿者、日期和班次不能为空");
        }

        VolunteerSchedule schedule;
        if (id != null) {
            schedule = scheduleMapper.selectById(id);
            if (schedule == null) {
                return Result.error("排班记录不存在");
            }
        } else {
            schedule = new VolunteerSchedule();
        }

        schedule.setVolunteerId(volunteerId);
        schedule.setScheduleDate(LocalDate.parse(date));
        schedule.setShiftType(shift);
        schedule.setRegion(region);
        schedule.setRemark(remark);

        if (id != null) {
            scheduleMapper.updateById(schedule);
        } else {
            scheduleMapper.insert(schedule);
        }

        return Result.success(null);
    }

    /**
     * 删除排班
     */
    @ApiOperation("删除排班")
    @DeleteMapping("/schedule/{id}")
    public Result<?> deleteSchedule(@PathVariable Long id) {
        scheduleMapper.deleteById(id);
        return Result.success(null);
    }

    /**
     * 获取积分变动记录
     */
    @ApiOperation("获取积分变动记录")
    @GetMapping("/points/log")
    public Result<PageResult<Map<String, Object>>> pointsLog(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult<Map<String, Object>> serviceResult =
                volunteerService.getPointsRecords(page, pageSize, null);

        // 转换字段名以匹配前端期望的格式，解析content获取积分变动
        List<Map<String, Object>> records = new ArrayList<>();
        if (serviceResult.getRecords() != null) {
            for (Map<String, Object> row : serviceResult.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                // 查志愿者名字
                Object vid = row.get("volunteerId");
                String name = "";
                if (vid != null) {
                    Volunteer v = volunteerMapper.selectById(Long.valueOf(vid.toString()));
                    if (v != null) name = v.getRealName();
                }
                item.put("volunteerName", name != null ? name : "");

                // 解析content: 格式 "兑换:物品名|-数量" 或其他
                String content = row.get("content") != null ? row.get("content").toString() : "";
                int change = 0;
                String action = "";
                if (content.contains("|")) {
                    String[] parts = content.split("\\|");
                    action = parts[0];
                    try { change = Integer.parseInt(parts[1]); } catch (Exception ignored) {}
                } else {
                    action = content;
                }
                item.put("action", action);
                item.put("change", change);

                // 查当前余额
                int balance = 0;
                if (vid != null) {
                    Volunteer v = volunteerMapper.selectById(Long.valueOf(vid.toString()));
                    if (v != null && v.getPoints() != null) balance = v.getPoints();
                }
                item.put("balance", balance);
                item.put("remark", content);
                item.put("createTime", row.get("createTime"));
                records.add(item);
            }
        }

        return Result.success(new PageResult<>(serviceResult.getTotal(), records));
    }

    /**
     * 获取积分规则列表
     */
    @ApiOperation("获取积分规则")
    @GetMapping("/points/rule")
    public Result<List<Map<String, Object>>> getPointsRules() {
        List<PointsRule> rules = pointsRuleMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (PointsRule r : rules) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("ruleKey", r.getRuleKey());
            item.put("ruleName", r.getRuleName());
            item.put("pointsValue", r.getPointsValue());
            item.put("unit", r.getUnit());
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 保存积分规则（批量更新）
     */
    @ApiOperation("保存积分规则")
    @PostMapping("/points/rule")
    public Result<?> savePointsRule(@RequestBody Map<String, Object> body) {
        Object rulesObj = body.get("rules");
        if (rulesObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rulesList = (List<Map<String, Object>>) rulesObj;
            for (Map<String, Object> ruleMap : rulesList) {
                String ruleKey = ruleMap.getOrDefault("ruleKey", "").toString();
                if (ruleKey.isEmpty()) continue;

                LambdaQueryWrapper<PointsRule> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PointsRule::getRuleKey, ruleKey);
                PointsRule existing = pointsRuleMapper.selectOne(wrapper);

                if (existing != null) {
                    existing.setRuleName(ruleMap.getOrDefault("ruleName", existing.getRuleName()).toString());
                    if (ruleMap.get("pointsValue") != null) {
                        existing.setPointsValue(new java.math.BigDecimal(ruleMap.get("pointsValue").toString()));
                    }
                    pointsRuleMapper.updateById(existing);
                }
            }
        }
        return Result.success(null);
    }

    /**
     * 获取兑换申请列表
     */
    @ApiOperation("获取兑换申请列表")
    @GetMapping("/exchange/list")
    public Result<List<Map<String, Object>>> exchangeList() {
        LambdaQueryWrapper<ExchangeRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ExchangeRequest::getCreateTime);
        List<ExchangeRequest> requests = exchangeRequestMapper.selectList(wrapper);

        // 预加载志愿者名称
        Map<Long, String> names = new HashMap<>();
        for (ExchangeRequest r : requests) {
            if (!names.containsKey(r.getVolunteerId())) {
                Volunteer v = volunteerMapper.selectById(r.getVolunteerId());
                names.put(r.getVolunteerId(), v != null ? v.getRealName() : "未知");
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ExchangeRequest r : requests) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("volunteerName", names.getOrDefault(r.getVolunteerId(), "未知"));
            item.put("itemName", r.getItemName());
            item.put("costPoints", r.getCostPoints());
            item.put("status", r.getStatus());
            item.put("createTime", r.getCreateTime());
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 审核兑换申请
     */
    @ApiOperation("审核兑换申请")
    @PostMapping("/exchange/{id}/review")
    public Result<?> reviewExchange(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "");
        ExchangeRequest request = exchangeRequestMapper.selectById(id);
        if (request == null) {
            return Result.error("兑换申请不存在");
        }
        if (!"pending".equals(request.getStatus())) {
            return Result.error("该申请已处理");
        }
        request.setStatus(status);
        exchangeRequestMapper.updateById(request);

        // 通过兑换申请：扣除积分 + 写日志
        if ("approved".equals(status)) {
            Volunteer volunteer = volunteerMapper.selectById(request.getVolunteerId());
            if (volunteer != null) {
                int currentPoints = volunteer.getPoints() != null ? volunteer.getPoints() : 0;
                int cost = request.getCostPoints() != null ? request.getCostPoints().intValue() : 0;
                volunteer.setPoints(currentPoints - cost);
                volunteerMapper.updateById(volunteer);

                // 写积分变动日志
                TaskLog log = new TaskLog();
                log.setVolunteerId(volunteer.getId());
                log.setAction("points");
                log.setContent("兑换:" + request.getItemName() + "|-" + cost);
                taskLogMapper.insert(log);
            }
        }

        return Result.success(null);
    }

    /**
     * 导出服务时长Excel
     * 查询所有志愿者并导出姓名、电话、累计时长、积分等数据
     */
    @ApiOperation("导出服务时长")
    @GetMapping("/export-hours")
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
            case 3: return "BLACKLISTED";
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
