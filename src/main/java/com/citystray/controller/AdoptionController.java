package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.annotation.OperationLog;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.AdoptionApply;
import com.citystray.entity.AdoptionVisit;
import com.citystray.entity.Animal;
import com.citystray.entity.RevisitRecord;
import com.citystray.service.AdoptionApplyService;
import com.citystray.service.NotificationService;
import com.citystray.entity.AdoptionApply;
import com.citystray.mapper.AdoptionApplyMapper;
import com.citystray.mapper.RevisitRecordMapper;
import com.citystray.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 领养管理控制器
 */
@Api(tags = "领养管理")
@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionApplyService adoptionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private RevisitRecordMapper revisitRecordMapper;

    /**
     * 分页查询领养申请列表
     */
    @ApiOperation("分页查询领养申请列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("申请状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        // 前端keyword对应realName, status对应stage
        PageResult<Map<String, Object>> serviceResult =
                adoptionService.getAdoptionList(page, pageSize, status, keyword);

        // 转换字段名以匹配前端期望的格式
        List<Map<String, Object>> transformedRecords = new ArrayList<>();
        if (serviceResult.getRecords() != null) {
            for (Map<String, Object> row : serviceResult.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", row.get("id"));
                item.put("animalName", getStr(row, "animal_name", "未知"));
                item.put("applicantName", getStr(row, "real_name", ""));
                item.put("applicantPhone", getStr(row, "phone", ""));
                item.put("status", row.get("stage"));
                item.put("createTime", row.get("create_time"));
                item.put("reviewerName", "");
                transformedRecords.add(item);
            }
        }

        return Result.success(new PageResult<>(serviceResult.getTotal(), transformedRecords));
    }

    /**
     * 获取领养申请详情
     */
    @SuppressWarnings("unchecked")
    @ApiOperation("获取领养申请详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @ApiParam("领养申请ID") @PathVariable Long id) {
        Map<String, Object> raw = adoptionService.getAdoptionDetail(id);

        AdoptionApply apply = (AdoptionApply) raw.get("apply");
        Animal animal = (Animal) raw.get("animal");
        List<AdoptionVisit> visits = (List<AdoptionVisit>) raw.get("visits");

        if (apply == null) {
            return Result.error("领养申请不存在");
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", apply.getId());
        item.put("animalName", animal != null ? animal.getName() : "未知");
        item.put("applicantName", apply.getRealName());
        item.put("applicantPhone", apply.getPhone());
        item.put("wechat", "");
        item.put("status", apply.getStage());
        item.put("createTime", apply.getCreateTime());
        item.put("housingType", apply.getLivingEnvironment());
        item.put("envPhotos", Collections.emptyList());
        item.put("petExperience", apply.getPetExperience());
        item.put("familyAgreed", apply.getFamilyConsent() != null && apply.getFamilyConsent() == 1);
        item.put("jobStatus", apply.getOccupation());
        item.put("address", apply.getAddress());
        item.put("reviewComment", apply.getReviewRemark());

        // 构造visitRecord(取最新一条家访记录)
        Map<String, Object> visitRecord = null;
        if (visits != null && !visits.isEmpty()) {
            AdoptionVisit latestVisit = visits.get(0);
            visitRecord = new LinkedHashMap<>();
            visitRecord.put("visitTime", latestVisit.getVisitDate());
            visitRecord.put("visitor", latestVisit.getVisitorName());
            visitRecord.put("result", latestVisit.getResult());
            visitRecord.put("photos", latestVisit.getPhotos());
            visitRecord.put("remark", latestVisit.getNotes());
        }
        item.put("visitRecord", visitRecord);

        return Result.success(item);
    }

    /**
     * 审核领养申请
     */
    @OperationLog(module = "领养管理", type = "UPDATE", content = "审核领养申请")
    @ApiOperation("审核领养申请")
    @PostMapping("/{id}/review")
    public Result<?> review(
            @ApiParam("领养申请ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String comment = body.getOrDefault("comment", "");
        adoptionService.reviewAdoption(id, status, comment, "");

        // 通知申请人审核结果
        try {
            AdoptionApply apply = adoptionApplyMapper.selectById(id);
            if (apply != null && apply.getUserId() != null) {
                String statusText = "approved".equals(status) ? "已通过" : "rejected".equals(status) ? "已拒绝" : status;
                String animalName = apply.getAnimalId() != null ? "您申请的领养" : "您的领养申请";
                notificationService.sendNotification(
                    apply.getUserId(),
                    "领养审核通知",
                    animalName + " 审核结果：" + statusText + (comment.isEmpty() ? "" : "，备注：" + comment),
                    "ADOPTION", "ADOPTION", id
                );
            }
        } catch (Exception e) {
            // 通知失败不影响主流程
        }

        return Result.success();
    }

    /**
     * 安排家访
     */
    @OperationLog(module = "领养管理", type = "UPDATE", content = "安排家访")
    @ApiOperation("安排家访")
    @PostMapping("/{id}/visit")
    public Result<?> arrangeVisit(
            @ApiParam("领养申请ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String visitTime = body.getOrDefault("visitTime", "");
        String visitor = body.getOrDefault("visitor", "");
        String remark = body.getOrDefault("remark", "");
        adoptionService.arrangeVisit(id, visitor, visitTime, remark);
        return Result.success();
    }

    /**
     * 开始试养阶段
     */
    @OperationLog(module = "领养管理", type = "UPDATE", content = "开始试养")
    @ApiOperation("开始试养")
    @PostMapping("/{id}/trial")
    public Result<?> startTrial(
            @ApiParam("领养申请ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String remark = body.getOrDefault("remark", "");
        adoptionService.reviewAdoption(id, "trial", remark, "");
        return Result.success();
    }

    /**
     * 确认正式领养
     */
    @OperationLog(module = "领养管理", type = "UPDATE", content = "确认正式领养")
    @ApiOperation("确认正式领养")
    @PostMapping("/{id}/confirm")
    public Result<?> confirmAdoption(
            @ApiParam("领养申请ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        adoptionService.confirmAdoption(id);
        return Result.success();
    }

    /**
     * 用户提交领养申请（小程序端）
     */
    @OperationLog(module = "领养管理", type = "CREATE", content = "提交领养申请")
    @ApiOperation("用户提交领养申请")
    @PostMapping("/apply")
    public Result<?> apply(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        AdoptionApply apply = new AdoptionApply();
        apply.setUserId(userId);
        apply.setAnimalId(body.get("animalId") != null ? Long.valueOf(body.get("animalId").toString()) : null);
        apply.setRealName((String) body.getOrDefault("realName", ""));
        apply.setPhone((String) body.getOrDefault("phone", ""));
        apply.setAge(body.get("age") != null ? Integer.valueOf(body.get("age").toString()) : null);
        apply.setOccupation((String) body.getOrDefault("occupation", ""));
        apply.setAddress((String) body.getOrDefault("address", ""));
        apply.setLivingEnvironment((String) body.getOrDefault("livingEnvironment", ""));
        apply.setPetExperience((String) body.getOrDefault("petExperience", ""));
        apply.setFamilyConsent(body.get("familyConsent") != null
                ? (Boolean.TRUE.equals(body.get("familyConsent")) ? 1 : 0) : 0);
        apply.setPhotos((String) body.getOrDefault("photos", ""));
        apply.setStage("submitted");

        adoptionService.save(apply);
        return Result.success(apply.getId());
    }

    /**
     * 获取我的领养申请（小程序端）
     */
    @ApiOperation("获取我的领养申请")
    @GetMapping("/my")
    public Result<PageResult<Map<String, Object>>> myApplications(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        LambdaQueryWrapper<AdoptionApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApply::getUserId, userId)
                .orderByDesc(AdoptionApply::getCreateTime);

        long total = adoptionApplyMapper.selectCount(wrapper);
        int offset = (page - 1) * pageSize;
        List<AdoptionApply> records = adoptionApplyMapper.selectList(
                wrapper.last("LIMIT " + offset + "," + pageSize));

        List<Map<String, Object>> list = new ArrayList<>();
        for (AdoptionApply a : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("animalId", a.getAnimalId());
            item.put("realName", a.getRealName());
            item.put("phone", a.getPhone());
            item.put("stage", a.getStage());
            item.put("createTime", a.getCreateTime());
            item.put("reviewRemark", a.getReviewRemark());
            list.add(item);
        }

        return Result.success(new PageResult<>(total, list));
    }

    /**
     * 回访记录列表
     */
    @ApiOperation("回访记录列表")
    @GetMapping("/revisit/list")
    public Result<PageResult<Map<String, Object>>> revisitList(
            @ApiParam("领养申请ID") @RequestParam(required = false) Long applyId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<RevisitRecord> wrapper = new LambdaQueryWrapper<>();
        if (applyId != null) {
            wrapper.eq(RevisitRecord::getApplyId, applyId);
        }
        wrapper.orderByDesc(RevisitRecord::getRevisitDate);

        Page<RevisitRecord> pageResult = revisitRecordMapper.selectPage(
                new Page<>(page, pageSize), wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (RevisitRecord r : pageResult.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("applyId", r.getApplyId());
            item.put("revisitDate", r.getRevisitDate());
            item.put("conditionDesc", r.getConditionDesc());
            item.put("healthStatus", r.getHealthStatus());
            item.put("isNormal", r.getIsNormal());
            item.put("photos", r.getPhotos());
            item.put("notes", r.getNotes());

            // 关联查询领养人和动物名称
            AdoptionApply apply = adoptionApplyMapper.selectById(r.getApplyId());
            if (apply != null) {
                item.put("adopterName", apply.getRealName());
                item.put("animalId", apply.getAnimalId());
            }
            records.add(item);
        }

        return Result.success(new PageResult<>(pageResult.getTotal(), records));
    }

    /**
     * 保存回访记录
     */
    @OperationLog(module = "领养管理", type = "CREATE", content = "保存回访记录")
    @ApiOperation("保存回访记录")
    @PostMapping("/revisit/save")
    public Result<?> saveRevisit(@RequestBody Map<String, Object> body) {
        RevisitRecord record = new RevisitRecord();

        Object applyIdObj = body.get("applyId");
        if (applyIdObj != null) {
            record.setApplyId(Long.valueOf(applyIdObj.toString()));
        }

        String revisitDate = (String) body.get("revisitDate");
        if (revisitDate != null && !revisitDate.isEmpty()) {
            record.setRevisitDate(java.time.LocalDate.parse(revisitDate));
        }

        record.setConditionDesc((String) body.get("conditionDesc"));
        record.setHealthStatus((String) body.getOrDefault("healthStatus", ""));
        record.setIsNormal(body.get("isNormal") != null ? Integer.valueOf(body.get("isNormal").toString()) : 1);
        record.setNotes((String) body.get("notes"));
        record.setPhotos(body.get("photos") != null ? body.get("photos").toString() : null);

        revisitRecordMapper.insert(record);
        return Result.success();
    }

    // ========== 辅助方法 ==========

    private String getStr(Map<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        if (val == null) return defaultValue;
        String s = val.toString();
        return s.isEmpty() ? defaultValue : s;
    }
}
