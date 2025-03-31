package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.ip.RequestIPContext;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final RequestIPContext requestIPContext;

  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    log.info("[로그인 시도] username: {}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("[로그인 실패] 회원을 찾을 수 없습니다 {}", username);
          return new AuthException(ErrorCode.USERNAME_MISMATCH, Map.of("username", username , "requestIp", requestIPContext.getClientIp()));
        });


    if (!user.getPassword().equals(password)) {
      log.warn("[로그인 실패} 잘못된 비밀번호 입니다: {}", username);
      throw new AuthException(ErrorCode.PASSWORD_MISMATCH, Map.of("username", username , "requestIp", requestIPContext.getClientIp()));
    }

    log.info("[로그인 성공] username: {}", user.getUsername());
    return userMapper.toDto(user);
  }
}
