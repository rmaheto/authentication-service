package com.codemaniac.authenticationservice.config;

import java.net.URL;
import java.util.Objects;
import javax.cache.Caching;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig implements CachingConfigurer {

  @Bean
  public JCacheCacheManager jCacheCacheManager() {
    return new JCacheCacheManager(ehCacheManager());
  }

  @Bean
  public javax.cache.CacheManager ehCacheManager() {
    URL myUrl = getClass().getResource("/ehcache.xml");
    org.ehcache.config.Configuration xmlConfig = new XmlConfiguration(Objects.requireNonNull(myUrl));
    EhcacheCachingProvider provider = (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
    return provider.getCacheManager(provider.getDefaultURI(),  xmlConfig);
  }

  @Override
  public KeyGenerator keyGenerator() {
    return new SimpleKeyGenerator();
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new SimpleCacheErrorHandler();
  }

}
