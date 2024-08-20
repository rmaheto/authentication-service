package com.codemaniac.authenticationservice.shared.utils;

import com.codemaniac.authenticationservice.exception.S3PropertyLoadException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Slf4j
public class S3PropertyConfig {

  @Bean
  @Profile("desktop")
  public PropertySourcesPlaceholderConfigurer desktopPropertySourcesPlaceholderConfigurer(
      Environment environment,
      @Qualifier("s3ClientConfigDesktop") S3Client s3ClientConfig) {

    return createPropertySourcesPlaceholderConfigurer(environment, s3ClientConfig);
  }

  @Bean
  @Profile("!desktop")
  public PropertySourcesPlaceholderConfigurer nonDesktopPropertySourcesPlaceholderConfigurer(
      Environment environment,
      @Qualifier("s3ClientConfigNonDesktop") S3Client s3ClientConfig) {

    return createPropertySourcesPlaceholderConfigurer(environment, s3ClientConfig);
  }

  private PropertySourcesPlaceholderConfigurer createPropertySourcesPlaceholderConfigurer(
      Environment environment,
      S3Client s3Client) {
    log.warn("Loading properties from S3 for {} environment...",
        Arrays.stream(environment.getActiveProfiles()).findFirst().orElse("default"));
    Properties properties = new Properties();
    String resolvedKey = getObjectKey(environment.getProperty("s3.key"));

    InputStream inputStream = AwsUtils.getS3ObjectContent(s3Client,
        environment.getProperty("s3.bucket"), resolvedKey);

    if (inputStream == null) {
      throw new S3PropertyLoadException("Failed to load properties from S3: InputStream is null.");
    }

    // Load properties from InputStream
    try {
      properties.load(inputStream);
    } catch (IOException e) {
      throw new S3PropertyLoadException("Failed to load properties from S3", e);
    }

    // Configure PropertySourcesPlaceholderConfigurer to use the loaded properties
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setProperties(properties);
    configurer.setIgnoreResourceNotFound(false);
    log.warn("properties from s3 {} environment loaded successfully",
        Arrays.stream(environment.getActiveProfiles()).findFirst());
    return configurer;
  }

  private String getObjectKey(String fileName) {
    Properties systemProperties = System.getProperties();
    String springActiveProfile = systemProperties.getProperty("serverLevel");
    return MessageFormat.format(fileName, springActiveProfile);
  }
}
