package org.openmrs.module.initializer.api;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.initializer.Domain;
import org.springframework.stereotype.Component;

@Component
public class CsvHeaders {
	
	private Map<Domain, CsvDomainHeaders> headers = new HashMap<Domain, CsvDomainHeaders>();
	
	@SuppressWarnings("unchecked")
	public void registerHeaders(Domain domain, Class<?> clazz,
	        Function<Field, ? super SimpleEntry<Field, Set<String>>> membersMapper) {
		Arrays.stream(clazz.getDeclaredFields()).filter(f -> java.lang.reflect.Modifier.isStatic(f.getModifiers()))
		        .filter(f -> StringUtils.startsWith(f.getName(), "HEADER_"))
		        .map((Function<Field, AbstractMap.SimpleEntry<Field, Set<String>>>) membersMapper).forEach(m -> {
			        try {
				        Field f = m.getKey();
				        addHeader(domain, (String) f.get(f), m.getValue());
			        }
			        catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
			        }
		        });
	}
	
	public void registerHeaders(Domain domain, Class<?> clazz) {
		Arrays.stream(clazz.getDeclaredFields()).filter(f -> java.lang.reflect.Modifier.isStatic(f.getModifiers()))
		        .filter(f -> StringUtils.startsWith(f.getName(), "HEADER_")).map(f -> {
			        return new AbstractMap.SimpleEntry<>(f, Collections.emptySet());
		        }).forEach(m -> {
			        try {
				        Field f = m.getKey();
				        addHeader(domain, (String) f.get(f), Collections.emptySet());
			        }
			        catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
			        }
		        });
	}
	
	public void addHeader(Domain domain, String header, Set<String> memberNames) {
		CsvDomainHeaders domainHeaders = new CsvDomainHeaders();
		if (headers.containsKey(domain)) {
			domainHeaders = headers.get(domain);
		}
		domainHeaders.addHeader(header, memberNames);
		headers.put(domain, domainHeaders);
	}
	
}
