package org.planqk.atlas.core.services;

import java.util.Optional;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProblemTypeServiceImpl implements ProblemTypeService {
	
	@Autowired
	private ProblemTypeRepository repo;

	@Override
	public ProblemType save(ProblemType problemType) {
		return repo.save(problemType);
	}

	@Override
	public Optional<ProblemType> getById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public Optional<ProblemType> getByName(String name) {
		return repo.findByName(name);
	}

	@Override
	public Page<ProblemType> getAll(Pageable pageable) {
		return repo.findAll(pageable);
	}

}
