package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@EnabledIfEnvironmentVariable(named = "RUN_S3_TEST", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class S3BinaryContentStorageTest {

  private static S3BinaryContentStorage storage;
  private static String bucket;
  private static UUID testId;
  private static final String testContent = "Hello S3 buckey binary content test!!";
  private static final String testFileName = "test-s3-jw.txt";

  @BeforeAll
  static void setup() {
    String accessKey = System.getenv("AWS_ACCESS_KEY");
    String secretKey = System.getenv("AWS_SECRET_KEY");
    String region = System.getenv("AWS_REGION");
    bucket = System.getenv("AWS_BUCKET");
    long expiration = Long.parseLong(System.getenv().getOrDefault("AWS_S3_PRESIGNED_URL_EXPIRATION", "600"));

    storage = new S3BinaryContentStorage(accessKey, secretKey, region, bucket, expiration);
  }

  @Test
  @Order(1)
  @DisplayName("S3에 파일을 업로드한다")
  void testPut() {
    testId = UUID.randomUUID();
    byte[] bytes = testContent.getBytes(StandardCharsets.UTF_8);

    UUID savedId = storage.put(testId, bytes);
    assertThat(savedId).isEqualTo(testId);
    log.info("Uploaded file ID: {}", testId);
  }

  @Test
  @Order(2)
  @DisplayName("S3에서 파일을 다운로드한다")
  void testGet() throws Exception {
    byte[] expected = testContent.getBytes(StandardCharsets.UTF_8);
    try (InputStream input = storage.get(testId)) {
      String loaded = new String(input.readAllBytes(), StandardCharsets.UTF_8);
      assertThat(loaded).isEqualTo(testContent);
      log.info("Downloaded content: {}", loaded);
    }
  }

  @Test
  @Order(4)
  @DisplayName("존재하지 않는 키로 get 요청 시 예외가 발생한다")
  void testGetWithNonExistingKey() {
    UUID nonExistingId = UUID.randomUUID();

    Assertions.assertThrows(NoSuchElementException.class, () -> {
      storage.get(nonExistingId);
    });

    log.info("Non-existing key test passed: {}", nonExistingId);
  }

  @Test
  @Order(3)
  @DisplayName("Presigned URL을 생성하여 리다이렉션한다")
  void testDownloadPresignedRedirect() {
    BinaryContentDto meta = new BinaryContentDto(
        testId,
        testFileName,
        (long) testContent.getBytes(StandardCharsets.UTF_8).length,
        "text/plain"
    );

    ResponseEntity<Void> response = (ResponseEntity<Void>) storage.download(meta);

    assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
    assertThat(response.getHeaders().getLocation()).isNotNull();
    log.info("Response header: {}", response.getHeaders());
  }
}