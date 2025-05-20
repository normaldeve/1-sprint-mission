package com.sprint.mission.discodeit.security.role;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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

  private final MessageRepository messageRepository;

  public void validateCanModifyUser(UUID targetUserId, Authentication auth) {
    UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
    UUID currentUserId = userDetails.getUser().getId();

    boolean isAdmin = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"));

    if (!currentUserId.equals(targetUserId) && !isAdmin) {
      throw new AccessDeniedException("해당 사용자에 대한 권한이 없습니다");
    }
  }

  public void validateCanDeleteMessage(Message message, Authentication auth) {
    UUID currentUserId = ((UserDetailsImpl) auth.getPrincipal()).getUser().getId();

    boolean isAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    boolean isAuthor = message.getAuthor().getId().equals(currentUserId);

    if (!isAuthor && !isAdmin) {
      throw new AccessDeniedException("해당 메시지에 대한 삭제 권한이 없습니다.");
    }
  }

  public void validateCanModifyMessage(UUID messageId, Authentication auth) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

    UUID currentUserId = ((UserDetailsImpl) auth.getPrincipal()).getUser().getId();

    boolean isAuthor = message.getAuthor().getId().equals(currentUserId);

    if (!isAuthor) {
      throw new AccessDeniedException("해당 메시지에 대한 수정 권한이 없습니다.");
    }
  }

  public void validateCanCreateOrModifyReadStatus(UUID userId, Authentication auth) {
    UUID currentUserId = ((UserDetailsImpl) auth.getPrincipal()).getUser().getId();
    boolean isAuthor = userId.equals(currentUserId);

    if (!isAuthor) {
      throw new AccessDeniedException("ReadStatus 권한이 없습니다.");
    }
  }
}
