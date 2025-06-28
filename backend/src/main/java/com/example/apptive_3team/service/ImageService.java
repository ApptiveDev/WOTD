package com.example.apptive_3team.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 이미지 업로드 함수
     * @param image
     * @return imageURL
     */
    public String upload(MultipartFile image) {
        try {
            // 고유값 생성(중복 방지)
            String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            metadata.setContentType(image.getContentType());

            // 요청 객체 생성
            PutObjectRequest request = new PutObjectRequest(
                    bucket,
                    imageName,
                    image.getInputStream(),
                    metadata
            );

            // S3에 업로드
            amazonS3Client.putObject(request);

            // 이미지 URL 반환
            return amazonS3Client.getUrl(bucket, imageName).toString();

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    /**
     * 이미지 삭제 함수
     * @param imageUrl
     */
    public void delete(String imageUrl) {
        try {
            // imageName 추출 (버킷 URL 이후 부분)
            String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // S3에서 객체 삭제
            amazonS3Client.deleteObject(bucket, imageName);
        } catch (Exception e) {
            throw new RuntimeException("S3 삭제 실패", e);
        }
    }

}
