package org.openmrs.module.initializer.api.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.openmrs.module.initializer.Domain;

@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface LineProcessor {
	
	Domain value();
	
}
