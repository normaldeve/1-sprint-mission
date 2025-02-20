package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.util.type.BinaryContentType;
import org.springframework.web.multipart.MultipartFile;

public record SaveFileRequest(BinaryContentType contentType, MultipartFile file) {
}
