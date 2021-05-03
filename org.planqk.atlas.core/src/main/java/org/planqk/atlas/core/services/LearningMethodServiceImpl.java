package org.planqk.atlas.core.services;

import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.core.repository.LearningMethodRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LearningMethodServiceImpl implements LearningMethodService {

    private final LearningMethodRepository learningMethodRepository;

    @Override
    public LearningMethod create(@NonNull LearningMethod learningMethod) {
        return learningMethodRepository.save(learningMethod);
    }

    @Override
    public Page<LearningMethod> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return learningMethodRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return learningMethodRepository.findAll(pageable);
    }

    @Override
    public LearningMethod findById(UUID learningMethodId) {
        return ServiceUtils.findById(learningMethodId, LearningMethod.class, learningMethodRepository);
    }

    @Override
    public LearningMethod update(LearningMethod learningMethod) {
        final LearningMethod persistedLearningMethod = findById(learningMethod.getId());

        persistedLearningMethod.setName(learningMethod.getName());

        return create(persistedLearningMethod);
    }

    @Override
    public void delete(UUID learningMethodId) {
        final LearningMethod learningMethod = findById(learningMethodId);

        if (learningMethod.getAlgorithms().size() > 0) {
            throw new EntityReferenceConstraintViolationException("Cannot delete LearningMethod with ID \"" + learningMethodId +
                    "\". It is used by existing algorithms!");
        }

        learningMethodRepository.deleteById(learningMethodId);
    }
}
