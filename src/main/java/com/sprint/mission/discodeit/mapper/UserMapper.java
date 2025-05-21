package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.session.SessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final SessionRegistry sessionRegistry;
  private final BinaryContentMapper binaryContentMapper;

  public UserDto toDto(User user) {
    boolean isOnline = sessionRegistry.isUserOnline(user.getId());
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        binaryContentMapper.toDto(user.getProfile()),
        isOnline,
        user.getRole()
    );
  }


}
