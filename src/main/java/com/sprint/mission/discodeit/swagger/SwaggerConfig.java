package com.sprint.mission.discodeit.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("디스코드 프로젝트 API")
            .description("디스코드와 비슷한 서비스를 제공하는 백엔드 API입니다")
            .version("1.0")
            .contact(new Contact()
                .email("junnukim1007@gmail.com")));
  }
}