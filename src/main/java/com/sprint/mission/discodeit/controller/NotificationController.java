package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final UserService userService;
  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> readNotifications(Authentication authentication) {
    UserDto user = userService.findByName(authentication.getName());

    return ResponseEntity.ok(notificationService.findAllByReceiverId(user.id()));
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(Authentication authentication, @PathVariable UUID notificationId) {
    UserDto user = userService.findByName(authentication.getName());
    notificationService.deleteByReceiverId(notificationId, user.id());

    return ResponseEntity.noContent().build();
  }
}
