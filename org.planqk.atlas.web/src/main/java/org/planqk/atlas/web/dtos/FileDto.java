package org.planqk.atlas.web.dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class FileDto {

    private UUID id;

    private UUID implementationId;

    private String name;

    private String mimeType;

    private String fileURL;
}
