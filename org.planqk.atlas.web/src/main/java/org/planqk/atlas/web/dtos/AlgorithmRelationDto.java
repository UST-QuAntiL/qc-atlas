/********************************************************************************
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

package org.planqk.atlas.web.dtos;


import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for Algorithms
 * ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class AlgorithmRelationDto extends RepresentationModel<AlgorithmRelationDto> {

	private Long id;
	
	private AlgorithmDto sourceAlgorithm;
	
	private AlgorithmDto targetAlgorithm;
	
	private AlgoRelationTypeDto algoRelationType;
	
	private String description;
	
	public static final class Converter {

		public static AlgorithmRelationDto convert(final AlgorithmRelation object) {
			final AlgorithmRelationDto dto = new AlgorithmRelationDto();
            dto.setId(object.getId());
            dto.setSourceAlgorithm(AlgorithmDto.Converter.convert(object.getSourceAlgorithm()));
            dto.setTargetAlgorithm(AlgorithmDto.Converter.convert(object.getTargetAlgorithm()));
            dto.setAlgoRelationType(AlgoRelationTypeDto.Converter.convert(object.getAlgoRelationType()));
            dto.setDescription(object.getDescription());
            return dto;
		}

		public static AlgorithmRelation convert(final AlgorithmRelationDto object) {
			final AlgorithmRelation algoRelation = new AlgorithmRelation();
			algoRelation.setId(object.getId());
			algoRelation.setSourceAlgorithm(AlgorithmDto.Converter.convert(object.getSourceAlgorithm()));
			algoRelation.setTargetAlgorithm(AlgorithmDto.Converter.convert(object.getTargetAlgorithm()));
			algoRelation.setAlgoRelationType(AlgoRelationTypeDto.Converter.convert(object.getAlgoRelationType()));
			algoRelation.setDescription(object.getDescription());
            
			return algoRelation;
		}
	}
}
