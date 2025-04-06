package com.codemaniac.authenticationservice.config.s3;


import com.codemaniac.authenticationservice.exception.S3PropertyLoadException;
import com.codemaniac.authenticationservice.shared.utils.AppUtils;
import com.codemaniac.authenticationservice.shared.utils.AwsUtils;
import io.findify.s3mock.S3Mock;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  public S3Client s3ClientConfig(final Environment environment) {
    log.warn("creating s3 mock and client for desktop environment...");
    // Start S3Mock server
    final S3Mock s3Mock = S3Mock.create(8005, "/tmp/s3");
    s3Mock.start();

    final S3Client s3ClientConfig = S3Client.builder()
        .endpointOverride(URI.create("http://localhost:8005"))
        .region(AwsUtils.getRegion())
        .credentialsProvider(DefaultCredentialsProvider.create())
        .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
        .build();

    uploadFile(environment, s3ClientConfig);

    return s3ClientConfig;
  }


  private void uploadFile(final Environment environment, final S3Client s3Client) {
    final String bucketName = environment.getProperty("s3.bucket");
    final String directoryName = environment.getProperty("properties.dir.name");
    final String desktopCredentialsFileName = environment.getProperty("properties.file.name");
    final Path filePath = AppUtils.resolveFilePath(directoryName, desktopCredentialsFileName);

    AwsUtils.createBucket(s3Client, bucketName);

    try (final InputStream resourceInputStream = new FileInputStream(filePath.toFile())) {
      AwsUtils.uploadFile(s3Client, bucketName, desktopCredentialsFileName, resourceInputStream,
          resourceInputStream.available());
      log.info("File uploaded to S3 Mock: {}/{}", bucketName, desktopCredentialsFileName);
    } catch (final IOException e) {
      log.error("Failed to upload the file from resources: {}", desktopCredentialsFileName, e);
      throw new S3PropertyLoadException("Failed to upload resource file", e);
    }
  }

}
