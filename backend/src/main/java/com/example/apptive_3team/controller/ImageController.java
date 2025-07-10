package com.example.apptive_3team.controller;

import com.example.apptive_3team.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("image") MultipartFile image) {
        log.info("📥 [POST] /images/upload API 호출됨");

        if (image == null || image.isEmpty()) {
            log.warn("⚠️ 업로드된 이미지 파일이 비어 있음");
            return ResponseEntity.badRequest().body("이미지 파일이 비어 있습니다.");
        }
        log.debug("📎 업로드된 파일명: {}, 크기: {} bytes", image.getOriginalFilename(), image.getSize());

        log.info("🛠 imageService.upload() 호출됨");
        String imageUrl = imageService.upload(image);

        log.info("✅ 업로드 성공 - 반환된 URL: {}", imageUrl);
        return ResponseEntity.ok(imageUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("imageUrl") String imageUrl) {
        log.info("📥 [DELETE] /images/delete API 호출됨");

        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("⚠️ 업로드된 이미지 파일 주소가 비어 있음");
            return ResponseEntity.badRequest().body("이미지 파일 주소가 비어 있습니다.");
        }

        log.debug("📎 업로드된 파일주소: {}", imageUrl);

        log.info("🛠 imageService.delete() 호출됨");
        imageService.delete(imageUrl);

        log.info("✅ 삭제 완료 - 반환된 URL: {}", imageUrl);
        return ResponseEntity.ok("삭제 완료!");
    }
}
