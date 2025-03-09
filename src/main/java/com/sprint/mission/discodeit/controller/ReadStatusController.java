package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ReadStatus가 생성되었는지 확인하기 위한 API
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @GetMapping
  public ResponseEntity<List<ReadStatus>> getStatusesByUserId(@RequestParam("userId") UUID userID) {
    List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userID);
    return ResponseEntity.ok(allByUserId);
  }

  @PostMapping
  public ResponseEntity<ReadStatus> createReadStatus(@RequestBody CreateReadStatusRequest request) {
    ReadStatus readStatus = readStatusService.create(request);
    return ResponseEntity.ok(readStatus);
  }

  @PatchMapping
  public ResponseEntity<ReadStatus> update(@PathParam("readStatusId") UUID readStatusId) {
    ReadStatus updateReadStatus = readStatusService.update(readStatusId);
    return ResponseEntity.ok(updateReadStatus);
  }
}
