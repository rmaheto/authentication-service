package com.codemaniac.authenticationservice.config.s3;

import com.codemaniac.authenticationservice.shared.utils.AwsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@Profile("!desktop")
public class S3ClientConfig {

  @Bean(name = "s3ClientConfigNonDesktop")
  public S3Client s3ClientConfig() {
    log.warn("creating s3 client for non-desktop environment...");
    return S3Client.builder()
        .region(AwsUtils.getRegion())
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }
}
