package com.yunho.shop.global.file;

import com.yunho.shop.global.exception.BusinessException;
import com.yunho.shop.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

/**
 * 이미지를 로컬 폴더에 저장한다.
 * 나중에 S3로 바꿀 때는 이 클래스만 교체하면 되도록 서비스와 분리해 둠.
 */
@Component
public class LocalImageStorage {

    private static final String URL_PREFIX = "/images/";

    private final Path rootDir;

    public LocalImageStorage(@Value("${app.upload.dir}") String uploadDir) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public StoredImage store(MultipartFile file) {
        validate(file);
        try {
            byte[] bytes = file.getBytes();
            String filename = UUID.randomUUID() + extractExtension(file.getOriginalFilename());

            Files.createDirectories(rootDir);
            Files.write(rootDir.resolve(filename), bytes);

            return new StoredImage(URL_PREFIX + filename, sha256(bytes));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.IMAGE_REQUIRED);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE);
        }
    }

    /** 같은 파일이면 항상 같은 64자리 값 → 나중에 LLM 태깅 캐시 키로 사용 */
    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", e);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
    }
}
