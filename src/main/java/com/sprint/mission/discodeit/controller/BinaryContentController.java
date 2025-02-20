package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.binarycontent.SaveFileRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @PostMapping("/upload")
    public ResponseEntity<BinaryContent> uploadFile(@ModelAttribute SaveFileRequest request) {
        try {
            BinaryContent binaryContent = binaryContentService.saveFile(request);
            return ResponseEntity.ok(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }
}
