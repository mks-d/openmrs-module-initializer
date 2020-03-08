package org.openmrs.module.initializer.api.annotations;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.openmrs.module.initializer.api.CsvHeaders;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CsvSerializedProcessor {
	
	@Autowired
	public void processCsvSerializedAnnotations(CsvHeaders headers) {
		
		Set<Field> fields = new HashSet<Field>();
		//		for(Class clazz : new Reflections("org.openmrs.module.initializer.api").getTypesAnnotatedWith(LineProcessorFor.class)) {
		//			fields.addAll(FieldUtils.getFieldsListWithAnnotation(clazz, CsvSerialized.class));
		//		}
		
		new Reflections("org.openmrs.module.initializer.api").getTypesAnnotatedWith(LineProcessor.class).stream()
		        .forEach(c -> fields.addAll(FieldUtils.getFieldsListWithAnnotation(c, CsvSerialized.class)));
		
		fields.stream().forEach(f -> System.out.println(f.getName()));
		
		//		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		//		
		//		scanner.addIncludeFilter(new AnnotationTypeFilter(LineProcessorFor.class));
		//		
		//		for (BeanDefinition bd : scanner.findCandidateComponents("org.openmrs.module.initializer.api")) {
		//			System.out.println(bd.getBeanClassName());
		//		}
		
		//		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(ConceptLineProcessor.class, CsvSerialized.class);
		
		"".toString();
		
	}
}
