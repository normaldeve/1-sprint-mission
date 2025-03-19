package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDTO;
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
    @PostMapping
    public ResponseEntity<ReadStatusDTO> create(@RequestBody CreateReadStatusRequest request) {
        ReadStatusDTO readStatus = readStatusService.create(request);
        return ResponseEntity.ok(readStatus);
    }

  @GetMapping
  public ResponseEntity<List<ReadStatusDTO>> findAllByUserId(@RequestParam("userId") UUID userId) {
    List<ReadStatusDTO> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatuses);
  }



  @PatchMapping(path = "{readStatusId}")
  public ResponseEntity<ReadStatusDTO> update(@PathParam("readStatusId") UUID readStatusId) {
    ReadStatusDTO updateReadStatus = readStatusService.update(readStatusId);
    return ResponseEntity.ok(updateReadStatus);
  }
}
