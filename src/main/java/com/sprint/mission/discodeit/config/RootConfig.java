package com.sprint.mission.discodeit.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {
    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // 필드 이름이 같으면 자동 매핑
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // private 필드에도 접근 가능
                .setMatchingStrategy(MatchingStrategies.STRICT); // 엄격한 매칭 전략 사용

        return modelMapper;
    }
}
