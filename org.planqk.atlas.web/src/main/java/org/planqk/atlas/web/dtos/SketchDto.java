package org.planqk.atlas.web.dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class SketchDto {

    private UUID id;

    private String imageURL;

    private String description;
}
