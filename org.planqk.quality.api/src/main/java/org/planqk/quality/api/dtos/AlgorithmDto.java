package org.planqk.quality.api.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for Algorithms ({@link org.planqk.quality.model.Algorithm}).
 */
public class AlgorithmDto extends RepresentationModel<AlgorithmDto> {

    private String name;

    @JsonCreator
    public AlgorithmDto(@JsonProperty("name") String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
