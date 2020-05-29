package org.planqk.atlas.web.utils;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

public class ModelMapperUtils {

	private static ModelMapper mapper = new ModelMapper();
	
	public static <D, T> Set<D> convertSet(Set<T> entities, Class<D> dtoClass) {
		Set<D> resultSet = new HashSet<D>();
		for (T entity: entities) {
			resultSet.add(convert(entity, dtoClass));
		}
		return resultSet;
	}

	public static <D, T> Page<D> convertPage(Page<T> entities, Class<D> dtoClass) {
		return entities.map(objectEntity -> mapper.map(objectEntity, dtoClass));
	}

	public static <D, T> D convert(final T entity, Class<D> outClass) {
		return mapper.map(entity, outClass);
	}
}
