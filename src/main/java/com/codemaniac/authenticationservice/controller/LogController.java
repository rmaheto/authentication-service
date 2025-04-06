package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.service.LogServiceImpl;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

  private final LogServiceImpl logService;

  @GetMapping
  public ResponseEntity<List<String>> listLogFiles() {
    try {
      final List<String> logFiles = logService.listLogFiles();
      return ResponseEntity.ok(logFiles);
    } catch (final IOException e) {
      return ResponseEntity.status(500).body(null);
    }
  }

  @GetMapping("/{fileName}")
  public ResponseEntity<String> readLogFile(@PathVariable final String fileName) {
    try {
      final String logContent = logService.readLogFile(fileName);
      return ResponseEntity.ok(logContent);
    } catch (final IOException e) {
      return ResponseEntity.status(500).body("Error reading log file: " + e.getMessage());
    }
  }
}