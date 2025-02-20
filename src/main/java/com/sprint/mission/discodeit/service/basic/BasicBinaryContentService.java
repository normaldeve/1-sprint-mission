package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.binarycontent.SaveFileRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent saveFile(SaveFileRequest request) throws IOException {
        if (request.file().isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String originalFileName = request.file().getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFileName; // 고유 저장 이름
        byte[] content = request.file().getBytes();

        BinaryContent binaryContent = new BinaryContent(content, storedFileName, request.contentType(), originalFileName, storedFileName);

        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public Optional<BinaryContent> find(UUID id) {
        return binaryContentRepository.findById(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> uuidList) {
        return binaryContentRepository.findAll().stream()
                .filter(bi -> uuidList.contains(bi.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
