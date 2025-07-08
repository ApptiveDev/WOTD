package com.example.apptive_3team.controller;

import com.example.apptive_3team.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("image") MultipartFile image) {
        String imageUrl = imageService.upload(image);
        return ResponseEntity.ok(imageUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("imageUrl") String imageUrl) {
        imageService.delete(imageUrl);
        return ResponseEntity.ok("삭제 완료!");
    }

}
