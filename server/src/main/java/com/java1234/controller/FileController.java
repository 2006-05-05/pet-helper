package com.java1234.controller;

import com.java1234.common.BusinessException;
import com.java1234.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 *
 * <p>所有上传的文件统一保存到 D 盘根目录的 uploads 目录下，
 * 并按日期建立子目录，返回可直接访问的相对地址。</p>
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    /** 文件上传根目录 */
    @Value("${pet.upload.path}")
    private String uploadPath;

    /** 文件访问前缀 */
    @Value("${pet.upload.prefix}")
    private String uploadPrefix;

    /** 允许上传的图片类型 */
    private static final List<String> ALLOW_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp");

    /**
     * 上传图片
     *
     * @param file 上传的文件
     * @return 文件访问地址
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        // 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOW_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("仅支持上传 jpg、png、gif、webp 格式的图片");
        }
        // 校验文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024L) {
            throw new BusinessException("图片大小不能超过 10MB");
        }
        try {
            // 按日期建立子目录：uploads/2026/11/19
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            File dir = new File(uploadPath, datePath);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new BusinessException("上传目录创建失败，请检查 D 盘 uploads 目录权限");
            }
            // 生成唯一文件名，避免重名覆盖
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // 返回可访问的相对路径
            String url = uploadPrefix + "/" + datePath + "/" + fileName;
            Map<String, Object> data = new HashMap<>(3);
            data.put("url", url);
            data.put("name", originalName);
            data.put("size", file.getSize());
            return Result.success("上传成功", data);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }
}
