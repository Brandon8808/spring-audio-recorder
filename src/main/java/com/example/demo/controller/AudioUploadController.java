package com.example.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AudioUploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // 上傳錄音檔
    @CrossOrigin
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            return ResponseEntity.ok("✅ 儲存成功：" + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 儲存失敗：" + e.getMessage());
        }
    }

    // 取得所有錄音檔名
    @CrossOrigin
    @GetMapping("/files")
    public List<String> listUploadedFiles() {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) return Collections.emptyList();

        String[] files = folder.list((dir, name) -> name.endsWith(".webm") || name.endsWith(".wav"));
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

    // 取得音訊檔案本體 (如果前端不用 static hosting 就開這個)
    @CrossOrigin
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveAudio(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
