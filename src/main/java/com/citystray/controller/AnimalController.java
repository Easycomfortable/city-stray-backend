package com.citystray.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citystray.annotation.OperationLog;
import com.citystray.common.PageResult;
import com.citystray.common.Result;
import com.citystray.entity.Animal;
import com.citystray.service.AnimalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 动物管理控制器
 */
@Api(tags = "动物管理")
@RestController
@RequestMapping("/api/animal")
@Slf4j
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    /**
     * 分页查询动物列表
     */
    @ApiOperation("分页查询动物列表")
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @ApiParam("关键词(名字/品种)") @RequestParam(required = false) String keyword,
            @ApiParam("健康状态") @RequestParam(required = false) String status,
            @ApiParam("物种") @RequestParam(required = false) String species,
            @ApiParam("性别(male/female)") @RequestParam(required = false) String gender,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Animal::getName, keyword)
                    .or().like(Animal::getBreed, keyword)
                    .or().like(Animal::getChipNo, keyword));
        }
        if (status != null && !status.isEmpty()) {
            if ("rescue".equals(status)) {
                // "待救助" = treating + recovering
                wrapper.in(Animal::getHealthStatus, "treating", "recovering");
            } else {
                wrapper.eq(Animal::getHealthStatus, status);
            }
        }
        if (species != null && !species.isEmpty()) {
            if ("cat".equals(species)) {
                wrapper.like(Animal::getBreed, "猫");
            } else if ("dog".equals(species)) {
                wrapper.and(w -> w.like(Animal::getBreed, "犬")
                        .or().like(Animal::getBreed, "狗"));
            } else if ("other".equals(species)) {
                wrapper.notLike(Animal::getBreed, "猫")
                        .notLike(Animal::getBreed, "犬")
                        .notLike(Animal::getBreed, "狗");
            }
        }
        if (gender != null && !gender.isEmpty()) {
            if ("male".equals(gender)) {
                wrapper.eq(Animal::getGender, 1);
            } else if ("female".equals(gender)) {
                wrapper.eq(Animal::getGender, 0);
            }
        }
        wrapper.orderByDesc(Animal::getCreateTime);

        IPage<Animal> pageResult = animalService.page(new Page<>(page, pageSize), wrapper);

        List<Map<String, Object>> records = new ArrayList<>();
        for (Animal a : pageResult.getRecords()) {
            records.add(toAnimalMap(a));
        }

        return Result.success(new PageResult<>(pageResult.getTotal(), records));
    }

    /**
     * 获取动物详情
     */
    @ApiOperation("获取动物详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @ApiParam("动物ID") @PathVariable Long id) {
        Animal animal = animalService.getAnimalById(id);
        return Result.success(toAnimalMap(animal));
    }

    /**
     * 新增动物
     */
    @OperationLog(module = "动物管理", type = "CREATE", content = "新增动物档案")
    @ApiOperation("新增动物")
    @PostMapping("/save")
    public Result<?> save(@RequestBody Animal animal) {
        animalService.addAnimal(animal);
        return Result.success();
    }

    /**
     * 更新动物信息
     */
    @OperationLog(module = "动物管理", type = "UPDATE", content = "更新动物信息")
    @ApiOperation("更新动物信息")
    @PutMapping("/{id}")
    public Result<?> update(
            @ApiParam("动物ID") @PathVariable Long id,
            @RequestBody Animal animal) {
        animal.setId(id);
        animalService.updateAnimal(animal);
        return Result.success();
    }

    /**
     * 删除动物（逻辑删除）
     */
    @OperationLog(module = "动物管理", type = "DELETE", content = "删除动物档案")
    @ApiOperation("删除动物")
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @ApiParam("动物ID") @PathVariable Long id) {
        animalService.deleteAnimal(id);
        return Result.success();
    }

    /**
     * 上传动物照片
     * 保存到uploads/目录并更新animal的photos字段
     */
    @ApiOperation("上传动物照片")
    @PostMapping("/{id}/photos")
    public Result<?> uploadPhotos(
            @ApiParam("动物ID") @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        Animal animal = animalService.getAnimalById(id);
        if (animal == null) {
            return Result.error("动物不存在");
        }

        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        try {
            // 创建上传目录
            String uploadDir = "uploads" + File.separator + "animal";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            File dest = new File(dir, filename);
            file.transferTo(dest);

            // 构建访问URL
            String url = "/uploads/animal/" + filename;

            // 更新photos字段（JSON数组格式追加）
            String existingPhotos = animal.getPhotos();
            String newPhotos;
            if (existingPhotos != null && existingPhotos.startsWith("[")) {
                // 追加到已有数组
                String inner = existingPhotos.substring(1, existingPhotos.length() - 1);
                if (inner.isEmpty()) {
                    newPhotos = "[\"" + url + "\"]";
                } else {
                    newPhotos = "[" + inner + ",\"" + url + "\"]";
                }
            } else {
                newPhotos = "[\"" + url + "\"]";
            }
            animal.setPhotos(newPhotos);
            animalService.updateAnimal(animal);

            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success(data);
        } catch (IOException e) {
            log.error("上传照片失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 导出动物数据为 Excel
     */
    @OperationLog(module = "动物管理", type = "EXPORT", content = "导出动物数据")
    @ApiOperation("导出动物数据")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        // 查询所有未删除的动物
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Animal::getCreateTime);
        List<Animal> animals = animalService.list(wrapper);

        // 构建导出数据
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Animal a : animals) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("动物编号", a.getAnimalNo());
            row.put("名称", a.getName());
            row.put("品种", a.getBreed());
            row.put("性别", a.getGender() != null ? (a.getGender() == 1 ? "公" : "母") : "");
            row.put("年龄估算", a.getAgeEstimate());
            row.put("体重(kg)", a.getWeight());
            row.put("毛色", a.getColor());
            row.put("是否绝育", a.getIsNeutered() != null && a.getIsNeutered() == 1 ? "是" : "否");
            row.put("健康状态", a.getHealthStatus());
            row.put("创建时间", a.getCreateTime());
            rows.add(row);
        }

        // 使用 Hutool 写出 Excel
        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        writer.write(rows);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=animals.xlsx");
        try {
            javax.servlet.ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            writer.close();
        } catch (IOException e) {
            log.error("导出动物数据失败", e);
        }
    }

    /**
     * 导入动物数据
     * 从Excel文件读取动物信息并批量新增
     */
    @OperationLog(module = "动物管理", type = "CREATE", content = "批量导入动物数据")
    @ApiOperation("导入动物数据")
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        try {
            cn.hutool.poi.excel.ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(file.getInputStream());
            List<Map<String, Object>> rows = reader.readAll();

            int successCount = 0;
            int failCount = 0;

            for (Map<String, Object> row : rows) {
                try {
                    Animal animal = new Animal();
                    animal.setName(getString(row, "名称"));
                    animal.setBreed(getString(row, "品种"));
                    animal.setAnimalNo(getString(row, "动物编号"));

                    String genderStr = getString(row, "性别");
                    if ("公".equals(genderStr)) animal.setGender(1);
                    else if ("母".equals(genderStr)) animal.setGender(0);

                    animal.setAgeEstimate(getString(row, "年龄估算"));

                    Object weightObj = row.get("体重(kg)");
                    if (weightObj != null) {
                        try {
                            animal.setWeight(new java.math.BigDecimal(weightObj.toString()));
                        } catch (Exception ignored) {}
                    }

                    animal.setColor(getString(row, "毛色"));

                    String neuterStr = getString(row, "是否绝育");
                    if ("是".equals(neuterStr)) animal.setIsNeutered(1);
                    else if ("否".equals(neuterStr)) animal.setIsNeutered(0);

                    animal.setHealthStatus(getString(row, "健康状态"));

                    animalService.addAnimal(animal);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                }
            }

            reader.close();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total", rows.size());
            result.put("success", successCount);
            result.put("fail", failCount);
            return Result.success(result);
        } catch (IOException e) {
            log.error("导入动物数据失败", e);
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    // ========== 辅助方法 ==========

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    /**
     * 根据品种名称推断物种
     */
    private String inferSpecies(String breed) {
        if (breed == null || breed.isEmpty()) return "other";
        if (breed.contains("猫")) return "cat";
        if (breed.contains("犬") || breed.contains("狗")) return "dog";
        return "other";
    }

    /**
     * 将Animal实体转换为前端期望的Map格式
     */
    private Map<String, Object> toAnimalMap(Animal a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("name", a.getName());

        // photo: 取photos字段(可能是JSON数组，取第一张或直接用整个字符串)
        String photos = a.getPhotos();
        String photo = "";
        if (photos != null && !photos.isEmpty()) {
            // 如果是JSON数组格式如 ["url1","url2"], 尝试取第一个
            if (photos.startsWith("[")) {
                try {
                    String trimmed = photos.substring(1, photos.length() - 1);
                    if (!trimmed.isEmpty()) {
                        String first = trimmed.split(",")[0].trim();
                        photo = first.replaceAll("^\"|\"$", "");
                    }
                } catch (Exception e) {
                    photo = photos;
                }
            } else {
                photo = photos;
            }
        }
        map.put("photo", photo);

        map.put("species", inferSpecies(a.getBreed()));
        map.put("breed", a.getBreed());
        map.put("gender", a.getGender());
        map.put("age", a.getAgeEstimate());
        map.put("weight", a.getWeight());
        map.put("spayed", a.getIsNeutered() != null && a.getIsNeutered() == 1);
        map.put("color", a.getColor());
        map.put("chipNumber", a.getChipNo());
        map.put("status", a.getHealthStatus());
        map.put("rescueOrderId", null);
        map.put("healthStatus", a.getHealthStatus());
        map.put("personality", "");
        map.put("rescueStory", a.getDescription());

        return map;
    }
}
