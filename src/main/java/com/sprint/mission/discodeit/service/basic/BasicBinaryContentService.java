package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final ModelMapper modelMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public BinaryContentDTO create(CreateBinaryContentRequest request) throws IOException {
    String filename = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = BinaryContent.builder()
            .fileName(filename)
            .contentType(contentType)
            .size((long) bytes.length)
            .build();

    BinaryContent saveBinaryContent = binaryContentRepository.save(binaryContent);

    binaryContentStorage.put(saveBinaryContent.getId(), bytes);

    return modelMapper.map(binaryContent, BinaryContentDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public BinaryContentDTO find(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_PROFILE));

    return modelMapper.map(binaryContent, BinaryContentDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public List<BinaryContentDTO> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentRepository.findAllIdIn(binaryContentIds);

    return binaryContents.stream()
            .map(binaryContent -> modelMapper.map(binaryContent, BinaryContentDTO.class))
            .toList();
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    binaryContentRepository.deleteById(binaryContentId);
  }
}
