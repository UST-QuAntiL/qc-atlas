package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ProblemType extends HasId {

    private String name;
    private UUID parentProblemType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "problemTypes")
    @EqualsAndHashCode.Exclude
    private Set<Algorithm> algorithms = new HashSet<>();

}
