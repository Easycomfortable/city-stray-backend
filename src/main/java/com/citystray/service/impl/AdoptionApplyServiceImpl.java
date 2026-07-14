package com.citystray.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citystray.common.PageResult;
import com.citystray.entity.*;
import com.citystray.mapper.*;
import com.citystray.service.AdoptionApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 领养申请服务实现类
 */
@Slf4j
@Service
public class AdoptionApplyServiceImpl extends ServiceImpl<AdoptionApplyMapper, AdoptionApply>
        implements AdoptionApplyService {

    @Autowired
    private AdoptionApplyMapper adoptionApplyMapper;

    @Autowired
    private AdoptionVisitMapper adoptionVisitMapper;

    @Autowired
    private AdoptionAgreementMapper adoptionAgreementMapper;

    @Autowired
    private AnimalMapper animalMapper;

    /**
     * 分页查询领养申请列表（关联申请人和动物信息）
     */
    @Override
    public PageResult<Map<String, Object>> getAdoptionList(Integer page, Integer size, String stage, String realName) {
        Map<String, Object> params = new HashMap<>();
        if (stage != null && !stage.isEmpty()) {
            params.put("stage", stage);
        }
        if (realName != null && !realName.isEmpty()) {
            params.put("realName", realName);
        }
        List<Map<String, Object>> allList = adoptionApplyMapper.selectApplyList(params);
        int total = allList.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> records = fromIndex < total
                ? allList.subList(fromIndex, toIndex) : Collections.emptyList();
        return new PageResult<>((long) total, records);
    }

    /**
     * 获取领养申请详情（含家访记录、协议、动物信息）
     */
    @Override
    public Map<String, Object> getAdoptionDetail(Long id) {
        AdoptionApply apply = this.getById(id);
        if (apply == null) {
            throw new RuntimeException("领养申请不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("apply", apply);

        // 查询关联的动物信息
        Animal animal = animalMapper.selectById(apply.getAnimalId());
        result.put("animal", animal);

        // 查询家访记录
        LambdaQueryWrapper<AdoptionVisit> visitWrapper = new LambdaQueryWrapper<>();
        visitWrapper.eq(AdoptionVisit::getApplyId, id)
                .orderByDesc(AdoptionVisit::getCreateTime);
        List<AdoptionVisit> visits = adoptionVisitMapper.selectList(visitWrapper);
        result.put("visits", visits);

        // 查询领养协议
        LambdaQueryWrapper<AdoptionAgreement> agreementWrapper = new LambdaQueryWrapper<>();
        agreementWrapper.eq(AdoptionAgreement::getApplyId, id);
        AdoptionAgreement agreement = adoptionAgreementMapper.selectOne(agreementWrapper);
        result.put("agreement", agreement);

        return result;
    }

    /**
     * 审核领养申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewAdoption(Long id, String stage, String remark, String rejectReason) {
        AdoptionApply apply = this.getById(id);
        if (apply == null) {
            throw new RuntimeException("领养申请不存在");
        }

        // 校验阶段流转合法性
        List<String> validStages = Arrays.asList(
                "submitted", "reviewing", "approved", "rejected", "visiting", "trial", "adopted");
        if (!validStages.contains(stage)) {
            throw new RuntimeException("非法的申请阶段：" + stage);
        }

        apply.setStage(stage);
        apply.setReviewRemark(remark);

        // 如果审核拒绝，设置拒绝原因
        if ("rejected".equals(stage)) {
            apply.setRejectReason(rejectReason);
        }

        this.updateById(apply);
        log.info("领养申请[{}]审核结果：{}，备注：{}", id, stage, remark);
    }

    /**
     * 安排家访
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void arrangeVisit(Long applyId, String visitorName, String visitDate, String notes) {
        AdoptionApply apply = this.getById(applyId);
        if (apply == null) {
            throw new RuntimeException("领养申请不存在");
        }

        // 创建家访记录
        AdoptionVisit visit = new AdoptionVisit();
        visit.setApplyId(applyId);
        visit.setVisitorName(visitorName);
        visit.setVisitDate(LocalDate.parse(visitDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        visit.setNotes(notes);
        adoptionVisitMapper.insert(visit);

        // 更新申请阶段为家访中
        apply.setStage("visiting");
        this.updateById(apply);

        log.info("领养申请[{}]已安排家访，回访人：{}，日期：{}", applyId, visitorName, visitDate);
    }

    /**
     * 确认正式领养
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmAdoption(Long applyId) {
        AdoptionApply apply = this.getById(applyId);
        if (apply == null) {
            throw new RuntimeException("领养申请不存在");
        }

        // 生成领养协议编号：AA + yyyyMMdd + 3位序列号
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AA" + dateStr;
        LambdaQueryWrapper<AdoptionAgreement> agWrapper = new LambdaQueryWrapper<>();
        agWrapper.likeRight(AdoptionAgreement::getAgreementNo, prefix);
        long count = adoptionAgreementMapper.selectCount(agWrapper);
        String seq = String.format("%03d", count + 1);
        String agreementNo = prefix + seq;

        // 创建领养协议
        AdoptionAgreement agreement = new AdoptionAgreement();
        agreement.setAgreementNo(agreementNo);
        agreement.setApplyId(applyId);
        agreement.setAnimalId(apply.getAnimalId());
        agreement.setAdopterName(apply.getRealName());
        agreement.setAdopterPhone(apply.getPhone());
        agreement.setSignDate(LocalDate.now());
        agreement.setContent("领养人承诺对所领养动物提供适宜的生活环境、定期医疗护理，并遵守相关领养协议条款。");
        adoptionAgreementMapper.insert(agreement);

        // 更新动物健康状态为已领养
        Animal animal = animalMapper.selectById(apply.getAnimalId());
        if (animal != null) {
            animal.setHealthStatus("adopted");
            animalMapper.updateById(animal);
        }

        // 更新申请阶段为已领养
        apply.setStage("adopted");
        this.updateById(apply);

        log.info("领养申请[{}]已确认正式领养，协议编号：{}", applyId, agreementNo);
    }

    /**
     * 获取家访记录
     */
    @Override
    public List<Map<String, Object>> getVisitRecords(Long applyId) {
        LambdaQueryWrapper<AdoptionVisit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionVisit::getApplyId, applyId)
                .orderByDesc(AdoptionVisit::getCreateTime);
        List<AdoptionVisit> visits = adoptionVisitMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (AdoptionVisit visit : visits) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", visit.getId());
            map.put("visitorName", visit.getVisitorName());
            map.put("visitDate", visit.getVisitDate());
            map.put("result", visit.getResult());
            map.put("evaluation", visit.getEvaluation());
            map.put("notes", visit.getNotes());
            map.put("photos", visit.getPhotos());
            map.put("createTime", visit.getCreateTime());
            result.add(map);
        }
        return result;
    }

    /**
     * 获取领养协议
     */
    @Override
    public Map<String, Object> getAgreement(Long applyId) {
        LambdaQueryWrapper<AdoptionAgreement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionAgreement::getApplyId, applyId);
        AdoptionAgreement agreement = adoptionAgreementMapper.selectOne(wrapper);

        if (agreement == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", agreement.getId());
        result.put("agreementNo", agreement.getAgreementNo());
        result.put("applyId", agreement.getApplyId());
        result.put("animalId", agreement.getAnimalId());
        result.put("adopterName", agreement.getAdopterName());
        result.put("adopterPhone", agreement.getAdopterPhone());
        result.put("adopterIdCard", agreement.getAdopterIdCard());
        result.put("signDate", agreement.getSignDate());
        result.put("content", agreement.getContent());
        result.put("createTime", agreement.getCreateTime());
        return result;
    }
}
