/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * Entity representing an implementation of a certain quantum {@link Algorithm}.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AuditTable("implementation_versions")
@Audited
public class Implementation extends VersionedKnowledgeArtifact {

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private String contributors;

    private String assumptions;

    private String parameter;

    private String dependencies;

    private String version;

    private String license;

    private String technology;

    @Column(columnDefinition = "text")
    private String problemStatement;

    @Column(columnDefinition = "text")
    private String inputFormat;

    @Column(columnDefinition = "text")
    private String outputFormat;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "implementation_publication",
            joinColumns = @JoinColumn(name = "implementation_id"),
            inverseJoinColumns = @JoinColumn(name = "publication_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    private Set<Publication> publications = new HashSet<>();

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    private Algorithm implementedAlgorithm;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "implementation_tag",
            joinColumns = @JoinColumn(name = "implementation_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_value"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    private Set<Tag> tags = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "implementation",
            orphanRemoval = true)
    @NotAudited
    private Set<ComputeResourceProperty> requiredComputeResourceProperties = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "implementation_software_platforms",
            joinColumns = @JoinColumn(name = "implementation_id"),
            inverseJoinColumns = @JoinColumn(name = "software_platform_id")
    )

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();


    @OneToMany(mappedBy = "implementation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    private Set<ImplementationPackage> implementationPackages = new HashSet<>();

    public void addTag(@NonNull Tag tag) {
        if (tags.contains(tag)) {
            return;
        }
        this.tags.add(tag);
        tag.addImplementation(this);
    }

    public void removeTag(@NonNull Tag tag) {
        if (!tags.contains(tag)) {
            return;
        }
        this.tags.remove(tag);
        tag.removeImplementation(this);
    }

    public void addPublication(@NonNull Publication publication) {
        if (publications.contains(publication)) {
            return;
        }
        publications.add(publication);
        publication.addImplementation(this);
    }

    public void removePublication(@NonNull Publication publication) {
        if (!publications.contains(publication)) {
            return;
        }
        publications.remove(publication);
        publication.removeImplementation(this);
    }

    public void addSoftwarePlatform(@NonNull SoftwarePlatform softwarePlatform) {
        if (softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.add(softwarePlatform);
        softwarePlatform.addImplementation(this);
    }

    public void removeSoftwarePlatform(@NonNull SoftwarePlatform softwarePlatform) {
        if (!softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.remove(softwarePlatform);
        softwarePlatform.removeImplementation(this);
    }

    public void addImplementationPackage(@NonNull ImplementationPackage implementationPackage) {
        if (implementationPackages.contains(implementationPackage)) {
            return;
        }
        this.implementationPackages.add(implementationPackage);
        implementationPackage.setImplementation(this);
    }

    public void removeImplementationPackage(@NonNull ImplementationPackage implementationPackage) {
        if (!implementationPackages.contains(implementationPackage)) {
            return;
        }
        this.implementationPackages.remove(implementationPackage);
        implementationPackage.setImplementation(null);
    }
}
