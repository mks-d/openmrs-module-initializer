/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
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
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class FlattenerTest extends BaseModuleContextSensitiveTest {
	
	public class ConceptClassSerializer extends StdSerializer<ConceptClass> {
		
		protected ConceptClassSerializer(Class<ConceptClass> t) {
			super(t);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void serialize(ConceptClass value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			jgen.writeStartObject();
			jgen.writeRaw(value.getUuid());
			//	        jgen.writeStringField("conceptClass", value.getUuid());
			jgen.writeEndObject();
		}
		
	};
	
	@Ignore
	@Test
	public void should_1() throws JsonProcessingException {
		
		CsvMapper mapper = new CsvMapper();
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(ConceptClass.class, new ConceptClassSerializer(ConceptClass.class));
		mapper.registerModule(module);
		
		Concept c = Context.getConceptService().getConcept(16);
		
		CsvSchema schema = mapper.schemaFor(Concept.class).withColumnSeparator(',');
		
		ObjectWriter writer = mapper.writer(schema);
		
		String s = writer.writeValueAsString(c);
		
		s.toString();
		
	}
	
	public abstract class IgnoreCreatorMixIn {
		
		@JsonIgnore
		abstract User getCreator();
	}
	
	public abstract class IgnoreConceptMixIn extends IgnoreCreatorMixIn {
		
		@JsonIgnore
		abstract Concept getConcept();
	}
	
	public abstract class ConceptMixIn extends IgnoreCreatorMixIn {
		
		@JsonIgnore
		abstract List<Concept> getPossibleValues();
	}
	
	public abstract class ConceptMapMixIn extends IgnoreConceptMixIn {
		
		@JsonIgnore
		abstract ConceptMapType getConceptMapType();
	}
	
	@Test
	public void should_2() {
		
		Concept c = Context.getConceptService().getConcept(16);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		mapper.addMixIn(ConceptDatatype.class, IgnoreCreatorMixIn.class);
		mapper.addMixIn(ConceptClass.class, IgnoreCreatorMixIn.class);
		mapper.addMixIn(ConceptReferenceTerm.class, IgnoreCreatorMixIn.class);
		mapper.addMixIn(ConceptSource.class, IgnoreCreatorMixIn.class);
		mapper.addMixIn(ConceptMap.class, ConceptMapMixIn.class);
		mapper.addMixIn(ConceptName.class, IgnoreConceptMixIn.class);
		mapper.addMixIn(ConceptDescription.class, IgnoreConceptMixIn.class);
		mapper.addMixIn(Concept.class, ConceptMixIn.class);
		
		// Convert POJO to Map
		Map<String, Object> map = mapper.convertValue(c, new TypeReference<Map<String, Object>>() {});
		
		"".toString();
		
	}
	
}
