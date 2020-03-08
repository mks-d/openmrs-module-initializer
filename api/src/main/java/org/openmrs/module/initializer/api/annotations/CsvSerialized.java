package org.openmrs.module.initializer.api.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CsvSerialized {
	
	String entityMember();
	
	Class<?> strategy();
	
}
