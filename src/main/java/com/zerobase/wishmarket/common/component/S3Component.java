package com.zerobase.wishmarket.common.component;

import static com.zerobase.wishmarket.exception.CommonErrorCode.EMPTY_FILE;
import static com.zerobase.wishmarket.exception.CommonErrorCode.FILE_DELETE_FAILED;
import static com.zerobase.wishmarket.exception.CommonErrorCode.FILE_UPLOAD_FAILED;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zerobase.wishmarket.exception.GlobalException;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Component {


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName; // Bucket 이름

    // 파일 확장자 구분선
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String SEPARATOR = "_";

    private final AmazonS3Client amazonS3Client;

    public String upload(String directory, String subDirectory, MultipartFile multipartFile) {
        validateFileExists(multipartFile);
        String fileName = buildFileName(directory, subDirectory, multipartFile.getOriginalFilename());
        long size = multipartFile.getSize(); // 파일 크기

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(size);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            log.info("이미지 업로드 : " + fileName);
            // S3에 업로드
            amazonS3Client.putObject(
                new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            throw new GlobalException(FILE_UPLOAD_FAILED);
        }

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }


    public void delete(String directory, String imageUrl) {
        try {
            int startIdx = imageUrl.indexOf(directory);
            String objectKey = imageUrl.substring(startIdx);
            System.out.println("삭제할 이미지 key : " + objectKey);
            amazonS3Client.deleteObject(bucketName, objectKey);
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
            throw new GlobalException(FILE_DELETE_FAILED);
        }
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new GlobalException(EMPTY_FILE);
        }
    }


    private String buildFileName(String directory, String category, String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return directory + "/" + category + "/" + fileName + SEPARATOR + now + fileExtension;
    }
}
