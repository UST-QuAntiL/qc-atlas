package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A compute resource is a QPU or a Simulator which are both able to run Quantum Algorithms. E.g. ibmq_rome or qasm_simulator.
 */

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ComputeResource extends HasId {

    private String name;
    private String vendor;
    private String technology;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "computeResource" ,orphanRemoval = true)
    private Set<ComputeResourceProperty> providedComputingResourceProperties = new HashSet<>();

    private QuantumComputationModel quantumComputationModel;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "supportedComputeResources")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "providedComputeResources")
    private Set<CloudService> cloudServices = new HashSet<>();

    public void addSoftwarePlatform(SoftwarePlatform softwarePlatform) {
        if (softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.add(softwarePlatform);
        softwarePlatform.addComputeResource(this);
    }

    public void removeSoftwarePlatform(SoftwarePlatform softwarePlatform) {
        if (!softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.remove(softwarePlatform);
        softwarePlatform.removeComputeResource(this);
    }

    public void addCloudService(CloudService cloudService) {
        if (cloudServices.contains(cloudService)) {
            return;
        }
        cloudServices.add(cloudService);
        cloudService.addComputeResource(this);
    }

    public void removeCloudService(CloudService cloudService) {
        if (!cloudServices.contains(cloudService)) {
            return;
        }
        cloudServices.remove(cloudService);
        cloudService.removeComputeResource(this);
    }
}
