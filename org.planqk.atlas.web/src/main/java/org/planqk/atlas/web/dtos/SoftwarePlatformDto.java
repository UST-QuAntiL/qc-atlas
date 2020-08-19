package org.planqk.atlas.web.dtos;

import java.net.URL;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@Relation(itemRelation = "softwarePlatform", collectionRelation = "softwarePlatforms")
public class SoftwarePlatformDto {

    @NotNull(groups = {ValidationGroups.Update.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a software platform")
    private UUID id;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "SoftwarePlatform name must not be null!")
    private String name;

    @Schema(description = "URL", example = "https://qiskit.org/")
    private URL link;

    private String licence;

    private String version;
}
