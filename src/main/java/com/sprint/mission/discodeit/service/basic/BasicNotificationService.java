package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.type.NotificationEvent;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public void deleteByReceiverId(UUID notificationId, UUID receiverId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException());

    if (!notification.getReceiver().getId().equals(receiverId)) {
      throw new AccessDeniedException("Not your notification");
    }

    notificationRepository.deleteByIdAndReceiverId(notificationId, receiverId);

  }

  @Async
  @Retryable(
      value = Exception.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000))
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotificationEvent(NotificationEvent event) {
    Notification notification = new Notification(
        event.receiver(),
        event.title(),
        event.content(),
        event.type(),
        event.targetId()
    );
    notificationRepository.save(notification);
    log.info("알림 저장 완료: user={}, title={}", event.receiver().getUsername(), event.title());
  }
}
