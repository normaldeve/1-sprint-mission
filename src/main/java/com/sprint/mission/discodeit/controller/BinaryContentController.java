package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping(value = "{binaryContentId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> download(@PathVariable UUID binaryContentId) {
        BinaryContentDTO binaryContentDTO = binaryContentService.find(binaryContentId);
        return (ResponseEntity<StreamingResponseBody>) binaryContentStorage.download(binaryContentDTO);
    }

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDTO> findFile(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDTO binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    @GetMapping()
    public ResponseEntity<List<BinaryContentDTO>> findAllByIdIn(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDTO> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(binaryContents);
    }
}
