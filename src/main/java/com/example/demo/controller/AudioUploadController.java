package com.example.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AudioUploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            // 假設接收到 webm，實際轉成 mp3（這裡為簡化，實作轉檔請依你已完成邏輯）
            String baseName = file.getOriginalFilename().replace(".webm", "");
            String mp3Name = baseName + ".mp3";
            Path mp3Path = Paths.get(UPLOAD_DIR, mp3Name);

            // TODO: 這裡實作 webm → mp3 的轉檔邏輯，你可自行調用 ffmpeg 等工具
            // 範例：Files.copy(file.getInputStream(), mp3Path, StandardCopyOption.REPLACE_EXISTING);
            // 暫時用原始檔假裝是 mp3（僅測試用）
            Files.copy(file.getInputStream(), mp3Path, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("上傳成功：" + mp3Name);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 儲存失敗：" + e.getMessage());
        }
    }

    @GetMapping("/files")
    public List<String> getFiles() {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) return Collections.emptyList();

        String[] files = folder.list((dir, name) -> name.endsWith(".mp3"));
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path path = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "audio/mpeg";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
