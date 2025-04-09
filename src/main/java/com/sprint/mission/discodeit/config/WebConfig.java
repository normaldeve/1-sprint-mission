package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final MDCLoggingInterceptor mdcLoggingInterceptor;

  @Autowired
  public WebConfig(MDCLoggingInterceptor mdcLoggingInterceptor) {
    this.mdcLoggingInterceptor = mdcLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(mdcLoggingInterceptor);
  }
}

