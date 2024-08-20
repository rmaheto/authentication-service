package com.codemaniac.authenticationservice.shared.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

public class AppUtils {

  private AppUtils() {
  }

  public static Path resolveFilePath(String directoryName, String resourceFileName) {
    Path currentDir = Paths.get(StringUtils.EMPTY).toAbsolutePath(); // Use StringUtils.EMPTY for clarity
    Path parentDir = currentDir.getParent(); // Move up to the parent directory
    return parentDir.resolve(directoryName).resolve(resourceFileName).normalize(); // Resolve the specified directory and file name
  }

}
