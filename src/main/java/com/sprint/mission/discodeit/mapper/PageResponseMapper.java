package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PageResponseMapper<T> {
    private final ModelMapper modelMapper;

    public PageResponse<T> fromPage(Page<T> page) {
        List<T> content = page.getContent().stream()
                .map(item -> modelMapper.map(item, (Class<T>) item.getClass()))
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
