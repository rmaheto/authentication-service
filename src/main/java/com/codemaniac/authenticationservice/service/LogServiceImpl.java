package com.codemaniac.authenticationservice.service;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl {

  private final Path logDirectory;

  public LogServiceImpl() {
    // Determine the root directory of the application and resolve the logs directory.
    final Path rootDirectory = Paths.get("").toAbsolutePath().normalize();
    this.logDirectory = rootDirectory.resolve("catalina.base_IS_UNDEFINED/logs").normalize();
  }
  public List<String> listLogFiles() throws IOException {
    try (final Stream<Path> paths = Files.walk(logDirectory)) {
      return paths
          .filter(Files::isRegularFile)
          .map(Path::getFileName)
          .map(Path::toString)
          .toList();
    }
  }

  public String readLogFile(final String fileName) throws IOException {
    final Path path = logDirectory.resolve(fileName);
    try {
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (final MalformedInputException e) {
      // Fallback to a more lenient encoding
      final byte[] fileBytes = Files.readAllBytes(path);
      return new String(fileBytes, StandardCharsets.ISO_8859_1);
    }
  }
}
