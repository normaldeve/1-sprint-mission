package com.sprint.mission.discodeit.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Profile("local")
@Component
@Log4j2
public class LocalBinaryContentStorage implements BinaryContentStorage {

    @Value("${discodeit.storage.local.root-path}")
    private Path root;

    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            log.info("Created directory: " + root);
        }
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] binaryContent) {
        Path targetPath = resolvePath(binaryContentId);
        try {
            Files.write(targetPath, binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + binaryContentId, e);
        }
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path targetPath = resolvePath(binaryContentId);
        if (!Files.exists(targetPath)) {
            throw new RuntimeException("File not found: " + binaryContentId);
        }
        try {
            return Files.newInputStream(targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> download(BinaryContentDTO binaryContentDTO) {
        UUID binaryContentId = binaryContentDTO.getId();
        Path targetPath = resolvePath(binaryContentId);

        if (!Files.exists(targetPath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            // InputStream을 try-with-resources로 안전하게 처리
            InputStream inputStream = Files.newInputStream(targetPath);

            StreamingResponseBody streamingResponseBody = outputStream -> {
                byte[] buffer = new byte[1024];
                int bytesRead;
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error during streaming file", e);
                } finally {
                    try {
                        inputStream.close();  // 스트림을 명시적으로 닫음
                    } catch (IOException e) {
                        // 스트림을 닫는 중 발생한 오류는 무시하고 넘어감
                    }
                }
            };

            // 응답을 반환
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + targetPath.getFileName().toString() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(streamingResponseBody);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }


    private Path resolvePath(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new IllegalArgumentException("binaryContentId cannot be null");
        }
        return root.resolve(binaryContentId.toString());
    }
}
