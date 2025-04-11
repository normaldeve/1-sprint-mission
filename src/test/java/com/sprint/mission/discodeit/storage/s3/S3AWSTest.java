package com.sprint.mission.discodeit.storage.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3AWSTest {

  private static AmazonS3 s3Client;
  private static String bucketName;

  @BeforeAll
  static void setUp() throws IOException {
    Properties properties = new Properties();
    try (InputStream input = new FileInputStream(".env.aws")) {
      properties.load(input);
    }

    String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
    String region = properties.getProperty("AWS_S3_REGION");
    bucketName = properties.getProperty("AWS_S3_BUCKET");

    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    s3Client = AmazonS3ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .build();
  }

  @Test
  @DisplayName("S3 버킷에 파일을 업로드합니다.")
  @Order(1)
  void upload() throws IOException {
    String key = "test-upload.txt";
    File file = Files.createTempFile("upload-test-", ".txt").toFile();

    try (FileWriter writer = new FileWriter(file)) {
      writer.write("Hello world!");
    }

    s3Client.putObject(new PutObjectRequest(bucketName, key, file));
    log.info("Upload complete: {}", key);
  }

  @Test
  @DisplayName("S3 버킷에 저장된 파일을 다운로드합니다")
  @Order(2)
  void download() throws IOException {
    String key = "test-upload.txt";
    S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));
    InputStream inputStream = s3Object.getObjectContent();

    String content = new String(inputStream.readAllBytes());
    log.info("Download complete: {}", content);
  }

  @Test
  @DisplayName("S3에 접근할 수 있는 프리사인 URL을 1시간 이내에 조회할 수 있습니다")
  @Order(3)
  void generatePresignedUrl() {
    String key = "test-upload.txt";
    Date expiration = Date.from(Instant.now().plusSeconds(3600));

    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
        .withMethod(HttpMethod.GET)
        .withExpiration(expiration);

    URL url = s3Client.generatePresignedUrl(request);
    log.info("Generate PresignedUrl complete: {}", url);
  }
}
