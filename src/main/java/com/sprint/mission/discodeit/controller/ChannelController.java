package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<ChannelDTO> createPublicChannel(
      @RequestBody CreateChannel.PublicRequest request) {
    ChannelDTO publicChannel = channelService.createPublicChannel(request);
    return ResponseEntity.ok(publicChannel);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelDTO> createPrivateChannel(
      @RequestBody CreateChannel.PrivateRequest request) {
    ChannelDTO privateChannel = channelService.createPrivateChannel(request);
    return ResponseEntity.ok(privateChannel);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDTO>> getAllChannels(@RequestParam("userId") UUID userId) {
    List<ChannelDTO> channels = channelService.findAllByUserId(userId);
      return ResponseEntity.ok(channels);
  }

  // 채널 정보 수정은 Public channel 만 가능합니다.
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDTO> updateChannel(
      @PathVariable("channelId") UUID channelId,
      @RequestBody UpdatePublicChannel request) {
    ChannelDTO update = channelService.update(channelId, request);
    return ResponseEntity.ok(update);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> deletePrivateChannel(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.ok(HttpStatus.NO_CONTENT.toString());
  }
}
