package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinaryContentDTO {
    private UUID id;

    @NotEmpty
    private String fileName;

    @NotNull
    private Long size;

    @NotEmpty
    private String contentType;

    @NotNull
    private byte[] bytes;
}
