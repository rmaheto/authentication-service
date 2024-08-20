package com.codemaniac.authenticationservice.shared.utils;

import java.io.InputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
public class AwsUtils {

  private AwsUtils() {
  }

  /**
   * Returns the resource name with the first occurrence of {0} replaced with the name of the
   * current AWS region.
   *
   * @param resourceName the resource name template, containing {0} as a placeholder for the region
   *                     name
   * @return the resource name with the region placeholder replaced, or the original resource name
   * if no placeholder is found
   */
  public static String getRegionSpecificAwsResourceName(String resourceName) {
    try {
      return MessageFormat.format(resourceName, getRegion().id().toLowerCase());
    } catch (Exception e) {
      log.warn(
          "ResourceName does not have injectable parameters for region. Using non-formatted resource name: "
              + resourceName);
      return resourceName;
    }
  }

  public static InputStream getS3ObjectContent(S3Client s3Client, String bucketName, String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    try {
      return s3Client.getObject(getObjectRequest);
    } catch (Exception e) {
      log.error("Failed to get object from S3: bucket={}, key={}", bucketName, key, e);
      return null;
    }
  }

  public static void uploadFile(S3Client s3Client, String bucketName, String key, String filePath) {
    // Upload the file
    s3Client.putObject(PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build(), RequestBody.fromFile(Paths.get(filePath)));
  }

  public static void uploadFile(S3Client s3Client, String bucketName, String key,
      InputStream inputStream, long contentLength) {
    // Upload the file directly from the InputStream
    s3Client.putObject(PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build(), RequestBody.fromInputStream(inputStream, contentLength));
  }

  public static Region getRegion() {
    try {
      DefaultAwsRegionProviderChain chain = DefaultAwsRegionProviderChain.builder().build();
      Region region = chain.getRegion();
      log.info("Using region: {}", region.id());
      return region;
    } catch (Exception e) {
      log.info("Failed to get region from provider, falling back to default us-east-1");
      return Region.US_EAST_1;
    }
  }
}
