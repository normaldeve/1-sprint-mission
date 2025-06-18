package com.sprint.mission.discodeit.security.role;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.security.user.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PermissionValidator {

  /**
   * 사용자 정보 수정 권한 인증
   *  - 본인이거나 관리자만 수정 가능
   */
  public void validateCanModifyUser(UUID targetUserId, Authentication auth) {

    if (targetUserId == null || auth == null) {
      throw new IllegalArgumentException("매개 변수가 null일 수 없습니다.");
    }

    UUID currentUserId = getCurrentUserId(auth);
    boolean isAdmin = isAdmin(auth);

    if (!currentUserId.equals(targetUserId) && !isAdmin) {
      throw new AccessDeniedException("해당 사용자에 대한 권한이 없습니다");
    }
  }

  /**
   * 메시지 삭제 권한 검증 - 작성자이거나 관리자만 가능
   */
  public void validateCanDeleteMessage(Message message, Authentication auth) {
    if (message == null || auth == null) {
      throw new IllegalArgumentException("매개변수가 null일 수는  없습니다.");
    }

    UUID currentUserId = getCurrentUserId(auth);
    boolean isAdmin = isAdmin(auth);
    boolean isAuthor = message.getAuthor().getId().equals(currentUserId);

    if (!isAuthor && !isAdmin) {
      throw new AccessDeniedException("해당 메시지에 대한 삭제 권한이 없습니다.");
    }
  }

  /**
   * 메시지 수정 권한 검증 - 작성자만 가능
   *
   */
  public void validateCanModifyMessage(Message message, Authentication auth) {

    UUID currentUserId = getCurrentUserId(auth);
    boolean isAuthor = message.getAuthor().getId().equals(currentUserId);

    if (!isAuthor) {
      throw new AccessDeniedException("해당 메시지에 대한 수정 권한이 없습니다.");
    }
  }

  /**
   * ReadStatus 생성/수정 권한 검증 - 본인만 가능
   *
   */
  public void validateCanCreateOrModifyReadStatus(UUID userId, Authentication auth) {
    if (userId == null || auth == null) {
      throw new IllegalArgumentException("매개변수는 null일 수 없습니다");
    }

    UUID currentUserId = getCurrentUserId(auth);

    if (!userId.equals(currentUserId)) {
      throw new AccessDeniedException("ReadStatus 권한이 없습니다");
    }
  }

  /**
   * 현재 인증된 사용자의 ID를 반환합니다.
   */
  private UUID getCurrentUserId(Authentication auth) {
    UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
    return userDetails.getUser().getId();
  }

  /**
   * 현재 인증된 사용자가 관리자 권한을 가지고 있는지 확인합니다.
   */
  private boolean isAdmin(Authentication auth) {
    return auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"));
  }
}
