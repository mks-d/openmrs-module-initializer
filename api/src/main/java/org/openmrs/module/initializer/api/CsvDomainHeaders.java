package org.openmrs.module.initializer.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CsvDomainHeaders {
	
	private Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
	
	public void addHeader(String header, Set<String> memberNames) {
		Set<String> allMemberNames = new HashSet<String>();
		if (headers.containsKey(header)) {
			allMemberNames = headers.get(header);
		}
		allMemberNames.addAll(memberNames);
		headers.put(header, allMemberNames);
	}
}
