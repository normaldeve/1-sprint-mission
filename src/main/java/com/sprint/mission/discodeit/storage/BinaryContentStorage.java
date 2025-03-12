package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
public interface BinaryContentStorage {
    UUID put(UUID binaryContentId, byte[] binaryContent);

    InputStream get(UUID binaryContentId);

    ResponseEntity<?> download(BinaryContentDTO binaryContentDTO);
}
