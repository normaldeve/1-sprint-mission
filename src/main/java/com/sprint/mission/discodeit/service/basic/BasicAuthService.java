package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.login.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService {

  private final UserRepository userRepository;
  
  public UserDTO login(LoginRequest request) { // 요청으로 name과 password가 들어온 상황
    User findUser = userRepository.findByName(request.username())
        .orElseThrow(() -> new ServiceException(ErrorCode.USERNAME_MISMATCH));

    if (!findUser.getPassword().equals(request.password())) { // 비밀번호가 일치하지 않으면 에러 발생
      throw new ServiceException(ErrorCode.PASSWORD_MISMATCH);
    }

    return UserDTO.fromDomain(findUser); // 비밀번호를 보여주지 않기 위해서 DTO 사용
  }
}
