package org.openmrs.module.initializer.api.c;

import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ConceptSerializer {
	
	public abstract class _IgnoreCreator {
		
		@JsonIgnore
		abstract User getCreator();
	}
	
	public abstract class _IgnoreConcept extends _IgnoreCreator {
		
		@JsonIgnore
		abstract Concept getConcept();
	}
	
	public abstract class _Concept extends _IgnoreCreator {
		
		@JsonIgnore
		abstract List<Concept> getPossibleValues();
	}
	
	public abstract class _ConceptMap extends _IgnoreConcept {
		
		@JsonIgnore
		abstract ConceptMapType getConceptMapType();
	}
	
	public Map<String, Object> toMap(Concept concept) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		mapper.addMixIn(ConceptDatatype.class, _IgnoreCreator.class);
		mapper.addMixIn(ConceptClass.class, _IgnoreCreator.class);
		mapper.addMixIn(ConceptReferenceTerm.class, _IgnoreCreator.class);
		mapper.addMixIn(ConceptSource.class, _IgnoreCreator.class);
		mapper.addMixIn(ConceptMap.class, _ConceptMap.class);
		mapper.addMixIn(ConceptName.class, _IgnoreConcept.class);
		mapper.addMixIn(ConceptDescription.class, _IgnoreConcept.class);
		mapper.addMixIn(Concept.class, _Concept.class);
		
		Map<String, Object> map = mapper.convertValue(concept, new TypeReference<Map<String, Object>>() {});
		
		return map;
	}
	
}
