package com.codemaniac.authenticationservice.config.s3;


import com.codemaniac.authenticationservice.shared.utils.AwsUtils;
import io.findify.s3mock.S3Mock;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Configuration
@Slf4j
@Profile("desktop")
public class LocalS3ClientConfig {

  @Bean(name = "s3ClientConfigDesktop")
  public S3Client s3ClientConfig(Environment environment) {
    log.warn("creating s3 mock and client for desktop environment...");
    // Start S3Mock server
    S3Mock s3Mock = S3Mock.create(8005, "/tmp/s3");
    s3Mock.start();

    S3Client s3ClientConfig = S3Client.builder()
        .endpointOverride(URI.create("http://localhost:8005"))
        .region(AwsUtils.getRegion())
        .credentialsProvider(DefaultCredentialsProvider.create())
        .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
        .build();

    uploadFile(environment,s3ClientConfig);

    return s3ClientConfig;
  }


  private void uploadFile(Environment environment,S3Client s3Client) {

    String bucketName = environment.getProperty("s3.bucket");
    createBucket(s3Client, bucketName);

    String resourceFileName = "credentials_desktop.properties";

    try (InputStream resourceInputStream = LocalS3ClientConfig.class.getClassLoader()
        .getResourceAsStream(resourceFileName)) {
      if (resourceInputStream != null) {
        // Upload the file using the overloaded uploadFile method
        AwsUtils.uploadFile(s3Client, bucketName, resourceFileName, resourceInputStream,
            resourceInputStream.available());

        log.info("File uploaded to S3 Mock: {}/{}", bucketName, resourceFileName);
      } else {
        log.error("Resource file not found in classpath: {}", resourceFileName);
        throw new RuntimeException("Resource file not found");
      }
    } catch (IOException e) {
      log.error("Failed to upload the file from resources: {}", resourceFileName, e);
      throw new RuntimeException("Failed to upload resource file", e);
    }
  }

  private static void createBucket(S3Client s3Client, String bucketName) {
    try {
      s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
      log.info("Bucket '{}' created successfully.", bucketName);
    } catch (Exception e) {
      log.warn("Bucket '{}' might already exist: {}", bucketName, e.getMessage());
    }
  }

}
