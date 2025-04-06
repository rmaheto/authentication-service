package com.codemaniac.authenticationservice.shared.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

public class AppUtils {

  private AppUtils() {
  }

  public static Path resolveFilePath(final String directoryName, final String resourceFileName) {
    final Path currentDir = Paths.get(StringUtils.EMPTY).toAbsolutePath();
    final Path parentDir = currentDir.getParent();
    return parentDir.resolve(directoryName).resolve(resourceFileName).normalize();
  }

}
