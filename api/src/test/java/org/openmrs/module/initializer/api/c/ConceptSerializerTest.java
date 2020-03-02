/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.c;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptMapType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.CsvHeaders;
import org.openmrs.module.initializer.api.c.ConceptSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class ConceptSerializerTest extends BaseModuleContextSensitiveTest {
	
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
	
	@Autowired
	private CsvHeaders csvHeaders;
	
	@Test
	public void should_2() {
		
		Concept c = Context.getConceptService().getConcept(16);
		
		ConceptSerializer szr = new ConceptSerializer();
		
		szr.toMap(c);
		
		"".toString();
		
	}
	
}
