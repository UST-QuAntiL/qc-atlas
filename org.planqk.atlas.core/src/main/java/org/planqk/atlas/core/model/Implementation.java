/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity representing an implementation of a certain quantum {@link Algorithm}.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Implementation extends AlgorOrImpl {

    private String name;
    private String description;
    private String contributors;
    private String assumptions;
    private String inputFormat;
    private String parameter;
    private String outputFormat;
    private URL link;
    private String dependencies;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Publication> publications = new HashSet<>();

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Algorithm implementedAlgorithm;

//    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @JoinTable(name = "implementation_tag", joinColumns = @JoinColumn(name = "implementation_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
//    private Set<Tag> tags;

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "implementation", orphanRemoval = true)
    private Set<ComputingResourceProperty> requiredComputingResourceProperties = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();

    public Implementation() {
        super();
    }

    public Set<Publication> getPublications() {
        return new HashSet<Publication>(publications);
    }

    public void addPublication(Publication publication) {
        if (publications.contains(publication)) {
            return;
        }
        publications.add(publication);
        publication.addImplementation(this);
    }

    public void removePublication(Publication publication) {
        if (!publications.contains(publication)) {
            return;
        }
        publications.remove(publication);
        publication.removeImplementation(this);
    }

    public Set<SoftwarePlatform> getSoftwarePlatforms() {
        return new HashSet<SoftwarePlatform>(softwarePlatforms);
    }

    public void addSoftwarePlatform(SoftwarePlatform softwarePlatform) {
        if (softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.add(softwarePlatform);
        softwarePlatform.addImplementation(this);
    }

    public void removeSoftwarePlatform(SoftwarePlatform softwarePlatform) {
        if (!softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.remove(softwarePlatform);
        softwarePlatform.removeImplementation(this);
    }

//    @NonNull
//    public Set<Tag> getTags() {
//        if (Objects.isNull(tags)) {
//            return new HashSet<>();
//        }
//        return tags;
//    }
}
