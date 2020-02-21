package org.planqk.quality.api.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for the model class Implementation ({@link org.planqk.quality.model.Implementation}).
 */
public class ImplementationDto extends RepresentationModel<ImplementationDto> {

    private String name;

    @JsonCreator
    public ImplementationDto(@JsonProperty("name") String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
