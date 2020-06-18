package org.planqk.atlas.core.model;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing a quantum simulator
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Data
public class Simulator extends Backend {

    private boolean localExecution;

    private String licence;

}
