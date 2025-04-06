package com.codemaniac.authenticationservice.shared.utils;

import jakarta.annotation.Nonnull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentHolder implements ApplicationContextAware {

  private static Environment environment;

  @Override
  public void setApplicationContext(@Nonnull final ApplicationContext ctx) {
    setEnvironment(ctx.getEnvironment());
  }

  private static void setEnvironment(final Environment env) {
    environment = env;
  }

  public static String getProperty(@Nonnull final String key) {
    return environment == null ? null : environment.getProperty(key);
  }
}
