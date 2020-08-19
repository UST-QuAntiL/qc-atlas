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
@Relation(itemRelation = "cloudService", collectionRelation = "cloudServices")
public class CloudServiceDto {

    @NotNull(groups = {ValidationGroups.Update.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a cloud service")
    private UUID id;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "CloudService name must not be null!")
    private String name;

    private String provider;

    @Schema(description = "URL", example = "https://www.ibm.com/quantum-computing/")
    private URL url;

    private String description;

    private String costModel;
}
