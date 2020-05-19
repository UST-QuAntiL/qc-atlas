package org.planqk.atlas.web.dtos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuantumResourceTypeListDto extends RepresentationModel<QuantumResourceTypeListDto> {

    @Getter
    private List<QuantumResourceTypeDto> items = new ArrayList<>();

    public void add(Collection<QuantumResourceTypeDto> items) {
        this.items.addAll(items);
    }

    public void add(QuantumResourceTypeDto item) {
        this.items.add(item);
    }
}
