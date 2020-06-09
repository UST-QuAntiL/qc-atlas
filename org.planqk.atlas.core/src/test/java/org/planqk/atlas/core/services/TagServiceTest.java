package org.planqk.atlas.core.services;

import org.planqk.atlas.core.repository.TagRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class TagServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    TagService tagService;

    @Autowired
    private TagRepository tagRepository;
}
