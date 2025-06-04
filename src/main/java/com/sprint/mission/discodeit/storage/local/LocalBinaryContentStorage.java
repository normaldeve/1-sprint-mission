package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.AsyncTaskFailure;
import com.sprint.mission.discodeit.repository.AsyncTaskFailureRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;
  private final AsyncTaskFailureRepository asyncTaskFailureRepository;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path root,
      AsyncTaskFailureRepository asyncTaskFailureRepository
  ) {
    this.root = root;
    this.asyncTaskFailureRepository = asyncTaskFailureRepository;
  }

  @PostConstruct
  public void init() {
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  @Async("asyncExecutor")
  @Retryable(
      value = {IOException.class, InterruptedException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 2000)
  )
  public CompletableFuture<Void> put(UUID binaryContentId, byte[] bytes) {
    Path filePath = resolvePath(binaryContentId);
    if (Files.exists(filePath)) {
      throw new IllegalArgumentException("File with key " + binaryContentId + " already exists");
    }
    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
      Thread.sleep(5000);
      outputStream.write(bytes);
      return CompletableFuture.completedFuture(null);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    Path filePath = resolvePath(binaryContentId);
    if (Files.notExists(filePath)) {
      throw new NoSuchElementException("File with key " + binaryContentId + " does not exist");
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private Path resolvePath(UUID key) {
    return root.resolve(key.toString());
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto metaData) {
    InputStream inputStream = get(metaData.id());
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + metaData.fileName() + "\"")
        .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
        .body(resource);
  }

  @Recover
  public UUID recover(IOException e, UUID binaryContentId, byte[] bytes) {
    String requestId = MDC.get("X-REQUEST-ID");

    AsyncTaskFailure failure = AsyncTaskFailure.builder()
        .taskName("LocalBinaryContentStorage")
        .requestId(requestId)
        .failureReason(e.getMessage())
        .build();

    asyncTaskFailureRepository.save(failure);

    log.error("Async upload task failed for {}: {}", binaryContentId, e.getMessage());

    return null;
  }
}
