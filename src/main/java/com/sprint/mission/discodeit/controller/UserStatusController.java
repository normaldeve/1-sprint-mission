package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// UserStatus가 제대로 생성되었는지 확인하는 API
@RestController
@RequestMapping("/api/userStatuses")
@RequiredArgsConstructor
public class UserStatusController {

  private final UserStatusService userStatusService;

  @GetMapping
  public ResponseEntity<List<UserStatus>> getAll() {
    List<UserStatus> all = userStatusService.findAll();
    return ResponseEntity.ok(all);
  }
}
