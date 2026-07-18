package com.citystray.controller;

import com.citystray.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 通用文件上传控制器
 * 支持单文件和多文件上传，文件存储在项目根目录的 uploads 文件夹下
 */
@Slf4j
@Api(tags = "文件上传")
@RestController
public class UploadController {

    /** 允许上传的图片类型 */
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    ));

    /** 最大文件大小：10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 单文件上传
     */
    @ApiOperation("单文件上传")
    @PostMapping("/api/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = saveFile(file);
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @ApiOperation("多文件上传")
    @PostMapping("/api/upload/batch")
    public Result<List<String>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                urls.add(saveFile(file));
            }
            return Result.success(urls);
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return Result.error("批量文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件到本地 uploads 目录
     */
    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 按日期分目录存储：uploads/2026/07/11/xxx.jpg
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadDir = "uploads" + File.separator + datePath;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(dir, fileName).getAbsoluteFile();
        file.transferTo(dest);

        log.info("文件上传成功: {}", dest.getAbsolutePath());

        // 返回访问URL（通过WebMvcConfig中配置的静态资源映射访问）
        return "/uploads/" + datePath + "/" + fileName;
    }
}
