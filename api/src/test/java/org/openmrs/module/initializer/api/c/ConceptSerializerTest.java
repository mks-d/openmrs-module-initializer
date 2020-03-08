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

import java.util.List;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMapType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.CsvHeaders;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ConceptSerializerTest extends BaseModuleContextSensitiveTest {
	
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
