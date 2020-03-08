package org.openmrs.module.initializer.api.c;

import static org.openmrs.module.initializer.Domain.CONCEPTS;
import static org.openmrs.module.initializer.api.c.LocalizedHeader.getLocalizedHeader;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvHeaders;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.annotations.CsvSerialized;
import org.openmrs.module.initializer.api.annotations.LineProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
@Component("initializer.conceptLineProcessor")
@LineProcessor(CONCEPTS)
public class ConceptLineProcessor extends BaseLineProcessor<Concept> {
	
	@CsvSerialized(entityMember = "names", strategy = ConceptShortNames.class)
	final public static String HEADER_SHORTNAME = "short name";
	
	final public static String HEADER_FSNAME = "fully specified name";
	
	//	@CsvSerialized(entityMember="conceptClass", strategy=SingleMetadata.class)
	final public static String HEADER_CLASS = "data class";
	
	//	@CsvSerialized(entityMember="dataType", strategy=SingleMetadata.class)
	final public static String HEADER_DATATYPE = "data type";
	
	protected ConceptService conceptService;
	
	@Autowired
	public ConceptLineProcessor(@Qualifier("conceptService") ConceptService conceptService, CsvHeaders csvHeaders) {
		this.conceptService = conceptService;
	}
	
	/*
	 * This is the base concept implementation.
	 */
	@Override
	public Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		LocalizedHeader lh = null;
		
		// Clearing existing names
		for (ConceptName cn : concept.getNames()) {
			concept.removeName(cn);
		}
		
		// Fully specified names
		lh = getLocalizedHeader(line.getHeaderLine(), HEADER_FSNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(name)) {
				ConceptName conceptName = new ConceptName(name, locale);
				concept.setFullySpecifiedName(conceptName);
			}
		}
		
		// Short names
		lh = getLocalizedHeader(line.getHeaderLine(), HEADER_SHORTNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(name)) {
				ConceptName conceptName = new ConceptName(name, locale);
				concept.setShortName(conceptName);
			}
		}
		
		// Descriptions
		if (!CollectionUtils.isEmpty(concept.getDescriptions())) {
			concept.getDescriptions().clear();
		}
		lh = getLocalizedHeader(line.getHeaderLine(), HEADER_DESC);
		for (Locale locale : lh.getLocales()) {
			String desc = line.get(lh.getI18nHeader(locale));
			if (!StringUtils.isEmpty(desc)) {
				ConceptDescription conceptDesc = new ConceptDescription(desc, locale);
				concept.addDescription(conceptDesc);
			}
		}
		
		// Concept data class
		String conceptClassName = line.getString(HEADER_CLASS, "");
		if (!StringUtils.isEmpty(conceptClassName)) {
			ConceptClass conceptClass = conceptService.getConceptClassByName(conceptClassName);
			if (conceptClass == null) {
				throw new IllegalArgumentException(
				        "Bad concept class name '" + conceptClassName + "' for line '" + line.toString() + "'.");
			}
			concept.setConceptClass(conceptClass);
		}
		
		// Concept data type
		String conceptTypeName = line.getString(HEADER_DATATYPE, "");
		if (!StringUtils.isEmpty(conceptTypeName)) {
			ConceptDatatype conceptDatatype = conceptService.getConceptDatatypeByName(conceptTypeName);
			if (conceptDatatype == null) {
				throw new IllegalArgumentException(
				        "Bad concept datatype name '" + conceptTypeName + "' for line '" + line.toString() + "'.");
			}
			concept.setDatatype(conceptDatatype);
		}
		
		return concept;
	}
}
