package org.openmrs.module.initializer.validator;

import static org.apache.commons.lang.StringUtils.endsWith;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.replace;
import static org.apache.commons.lang.StringUtils.startsWith;
import static org.openmrs.module.initializer.InitializerConstants.ARG_DOMAINS;
import static org.openmrs.module.initializer.InitializerConstants.ARG_EXCLUDE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.runner.JUnitCore;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.utils.Utils;

public class Validator {
	
	public static CommandLine cmdLine;
	
	public static final String ARG_CONFIG_DIR = "config-dir";
	
	public static final String ARG_CIEL_PATH = "ciel-path";
	
	public static final String ARG_HELP = "help";
	
	public static Set<org.apache.log4j.spi.LoggingEvent> errors = new HashSet<>();
	
	/**
	 * Properly escapes the single quotes of a piece of SQL for MySQL. Strings ending with a ";" are
	 * assumed to be the last piece of a SQL instruction.
	 * 
	 * @param sqlPiece A whole SQL instruction or a piece of SQL instruction
	 * @return The piece of SQL with single quotes properly escaped
	 * @see https://stackoverflow.com/a/64878054/321797
	 */
	public static String escapeSingleQuotes(String sqlPiece) {
		boolean needsReappending = false;
		if (endsWith(sqlPiece, ";")) {
			needsReappending = true;
			sqlPiece = removeEnd(sqlPiece, ";");
		}
		return replace(sqlPiece, "\\'", "''") + (needsReappending ? ";" : "");
	}
	
	/**
	 * Turns an original CIEL SQL dump into a HSQLDB-friendly one.
	 */
	public static File trimCielSqlFile(File originalCielSqlFile) throws IOException {
		File trimmedCielSqlFile = File.createTempFile("iniz-validator-trimmed-ciel", ".sql");
		trimmedCielSqlFile.deleteOnExit();
		
		final BufferedWriter writer = new BufferedWriter(new FileWriter(trimmedCielSqlFile));
		for (String line : Files.readAllLines(Paths.get(originalCielSqlFile.getAbsolutePath()), Charsets.UTF_8)) {
			if (!startsWith(line, "--") && !startsWith(line, "/*!40") && !isEmpty(line)) {
				writer.write(escapeSingleQuotes(line + "\n"));
			}
		}
		writer.close();
		
		return trimmedCielSqlFile;
	}
	
	public static File getJarFile() throws URISyntaxException {
		CodeSource codeSource = Validator.class.getProtectionDomain().getCodeSource();
		return new File(codeSource.getLocation().toURI().getPath());
	}
	
	public static String getJarDirPath() throws URISyntaxException {
		File jarFile = getJarFile();
		String jarDir = jarFile.getParentFile().getPath();
		return jarDir;
	}
	
	public static Options getCLIOptions() {
		final StringBuilder sb = new StringBuilder();
		Stream.of(Domain.values()).forEach(d -> {
			sb.append(d.getName() + ",");
		});
		final String domainsCsv = removeEnd(sb.toString(), ",");
		
		Options options = new Options();
		options.addOption("h", ARG_HELP, false, "Prints help.");
		options.addOption("c", ARG_CONFIG_DIR, true, "<arg>: the path to the OpenMRS config directory.");
		options.addOption("l", ARG_CIEL_PATH, true, "<arg>: the path to the CIEL .sql dump file.");
		options.addOption("d", ARG_DOMAINS, true,
		    "<arg>: a CSV string of selected domains to selectively include, eg.: metadatasharing,concepts,roles ;"
		            + "\nor to selectively exclude, eg.: !metadatasharing,concepts,roles ;"
		            + "\nomit the argument altogether to process all domains." + "\nAvailable domains: " + domainsCsv);
		
		Stream.of(Domain.values()).forEach(d -> {
			options.addOption(null, ARG_EXCLUDE + "." + d.getName(), true,
			    "<arg>: a CSV string of wildcard file exclusion patterns to apply to the '" + d.getName() + "' domain."
			            + "\nEg.: *foo*.bar,*f00*.baz");
		});
		return options;
	}
	
	public static void main(String[] args) throws URISyntaxException, ParseException {
		// setting up logging
		{
			org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("org.openmrs.module.initializer");
			logger.addAppender(Utils.getFileAppender(Paths.get(getJarDirPath(), "initializer.log")));
			logger.addAppender(new ValidatorAppender());
			logger.setLevel(org.apache.log4j.Level.WARN);
			
			//			org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
			org.apache.log4j.Logger.getLogger("org.openmrs").setLevel(org.apache.log4j.Level.INFO);
			org.apache.log4j.Logger.getLogger("org.openmrs.api").setLevel(org.apache.log4j.Level.INFO);
		}
		
		// processing args
		{
			Options options = getCLIOptions();
			cmdLine = new DefaultParser().parse(options, args);
			
			if (ArrayUtils.isEmpty(cmdLine.getOptions()) || cmdLine.hasOption(ARG_HELP)) {
				new HelpFormatter().printHelp(getJarFile().getName(), options);
				return;
			}
		}
		
		//		JUnitCore junit = new JUnitCore();
		//		Result result = junit.run(ConfigValidationTest.class);
		//		System.out.println("Success: " + result.wasSuccessful());
		
		JUnitCore.main(ConfigurationTester.class.getCanonicalName());
		
	}
}
