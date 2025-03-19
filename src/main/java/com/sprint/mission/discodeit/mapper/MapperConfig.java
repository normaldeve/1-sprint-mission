package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Slice;

import java.util.UUID;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // 필드 이름이 같으면 자동 매핑
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // private 필드에도 접근 가능
                .setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<UserStatus, UUID> userToUserIdConverter = ctx -> ctx.getSource().getUser().getId();

        modelMapper.typeMap(UserStatus.class, UserStatusDTO.class)
                .addMappings(mapper -> mapper.using(userToUserIdConverter).map(src -> src, UserStatusDTO::setUserId));


        return modelMapper;
    }
}
