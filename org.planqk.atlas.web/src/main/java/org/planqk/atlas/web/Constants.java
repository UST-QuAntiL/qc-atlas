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

package org.planqk.atlas.web;

/**
 * Constants for the QC-Atlas API classes.
 */
public final class Constants {

    // URL snippets
    public static final String ALGORITHMS = "algorithms";

    public static final String ALGORITHM_RELATIONS = "algorithm-relations";

    public static final String ALGORITHM_RELATION_TYPES = "algorithm-relation-types";

    public static final String APPLICATION_AREAS = "application-areas";

    public static final String CLOUD_SERVICES = "cloud-services";

    public static final String COMPUTE_RESOURCES = "compute-resources";

    public static final String COMPUTE_RESOURCE_PROPERTIES = "compute-resource-properties";

    public static final String FILES = "files";

    public static final String FILE = "file";

    public static final String COMPUTE_RESOURCE_PROPERTY_TYPES = "compute-resource-property-types";

    public static final String DISCUSSION_COMMENTS = "discussion-comments";

    public static final String DISCUSSION_TOPICS = "discussion-topics";

    public static final String IMPLEMENTATIONS = "implementations";

    public static final String IMPLEMENTATION_PACKAGES = "implementation-packages";

    public static final String PATTERN_RELATIONS = "pattern-relations";

    public static final String PATTERN_RELATION_TYPES = "pattern-relation-types";

    public static final String PROBLEM_TYPES = "problem-types";

    public static final String PROBLEM_TYPE_PARENTS = "problem-type-parents";

    public static final String PUBLICATIONS = "publications";

    public static final String SKETCHES = "sketches";

    public static final String SOFTWARE_PLATFORMS = "software-platforms";

    public static final String TAGS = "tags";

    public static final String REVISIONS = "revisions";

    // default Pagination params that are exposed in HATEOAS links
    public static final Integer DEFAULT_PAGE_NUMBER = 0;

    public static final Integer DEFAULT_PAGE_SIZE = 50;

    // query parameter names
    public static final String PAGE = "page";

    public static final String SIZE = "size";

    public static final String SEARCH = "search";

    // Swagger tag names
    public static final String TAG_ALGORITHM = "algorithm";

    public static final String TAG_ALGORITHM_RELATIONS = "algorithm-relations";

    public static final String TAG_ALGORITHM_RELATION_TYPE = "algorithm-relation-type";

    public static final String TAG_APPLICATION_AREAS = "application-areas";

    public static final String TAG_COMPUTE_RESOURCE_PROPERTIES = "compute-resource-properties";

    public static final String TAG_COMPUTE_RESOURCE_PROPERTY_TYPES = "compute-resource-property-types";

    public static final String TAG_DISCUSSION_TOPIC = "discussion-topic";

    public static final String TAG_EXECUTION_ENVIRONMENTS = "execution-environments";

    public static final String TAG_IMPLEMENTATIONS = "implementations";

    public static final String TAG_PATTERN_RELATION = "pattern-relation";

    public static final String TAG_PATTERN_RELATION_TYPE = "pattern-relation-type";

    public static final String TAG_PROBLEM_TYPE = "problem-type";

    public static final String TAG_PUBLICATION = "publication";

    public static final String TAG_ROOT = "root";

    public static final String TAG_TAG = "tag";


    private Constants() {
    }
}
