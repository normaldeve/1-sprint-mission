package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@EnableRetry
@EnableCaching
public class AppConfig {

  @Bean
  public TaskDecorator contextDecorator() {
    return runnable -> {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      SecurityContext context = SecurityContextHolder.getContext();

      return () -> {
        try {
          MDC.setContextMap(contextMap);
          SecurityContextHolder.setContext(context);
          runnable.run();
        } finally {
          MDC.clear();
          SecurityContextHolder.clearContext();
        }
      };
    };
  }

  @Bean(name = "asyncExecutor")
  public Executor asyncExecutor(TaskDecorator contextDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("async-exec-");
    executor.setTaskDecorator(contextDecorator);
    executor.initialize();
    return executor;
  }

  @Bean
  TimedAspect timedAspect(MeterRegistry meterRegistry){
    return new TimedAspect(meterRegistry);
  }

  @Bean
  public CacheManager cashManager() {
    Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(10000);

    return new CaffeineCacheManager("channelsByUser", "notificationsByUser", "userList");
  }
}