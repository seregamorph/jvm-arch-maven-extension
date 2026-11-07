package com.github.seregamorph.maven.arch;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.SessionScoped;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Properties;

/**
 * Maven extension to validate the architecture of the current JVM vs real CPU to avoid overhead of
 * Rosetta or Windows x64 emulation on ARM.
 */
@SessionScoped
@Named
public class JvmArchExtension extends AbstractMavenLifecycleParticipant {

    private static final Logger logger = LoggerFactory.getLogger(JvmArchExtension.class);

    private static final String ANSI_RED = "\u001B[31m";

    private static final String PROP_SKIP_JVM_ARCH = "skipJvmArch";

    /**
     * Extension behaviour in case of JVM arch does not match the real CPU arch.
     * Possible values: FAIL, WARN.
     */
    private Policy policy = Policy.FAIL;
    private String additionalFailureMessage;

    private boolean executedOnce;
    /**
     * Set to true if arch validation was successful on afterProjectsRead to avoid second execution
     */
    private boolean passed;

    @Inject
    public JvmArchExtension(MavenSession session) {
        File propertiesFile = new File(session.getExecutionRootDirectory(), ".mvn/jvm-arch.properties");
        if (propertiesFile.exists()) {
            logger.debug("Loading properties from {}", propertiesFile);
            Properties properties = new Properties();
            try (InputStream in = new FileInputStream(propertiesFile)) {
                properties.load(in);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to load " + propertiesFile, e);
            }
            try {
                policy = Policy.valueOf(properties.getProperty("policy", "FAIL"));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error while reading " + propertiesFile, e);
            }
            additionalFailureMessage = properties.getProperty("additionalFailureMessage");
        }
    }

    @Override
    public void afterSessionStart(MavenSession session) throws MavenExecutionException {
        // Implementation notice: this method is called once in CLI execution, once in Maven execution via IDEA
        // (specifies "idea.version" property) and once in IDEA import (IDEA also specifies "idea.version" and
        // "idea.maven.embedder.version" properties).
        // But it is NOT executed if the extension is defined via <build><extensions> in pom.xml
    }

    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        // Implementation notice: this method is called once in CLI execution with the whole set of modules,
        // same for run Maven from IDEA (specifies "idea.version" property), but
        // runs once PER EACH module during the IDEA import.
        // It is also executed if the extension is defined via <build><extensions> in pom.xml
        if (!executedOnce) {
            executedOnce = true;
            if (exec()) {
                passed = true;
            }
        }
    }

    @Override
    public void afterSessionEnd(MavenSession session) throws MavenExecutionException {
        // Implementation notice: this method is called once in CLI execution, same while running Maven from IDEA
        // and never during the IDEA reimport
        // It is also executed if the extension is defined via <build><extensions> in pom.xml
        if (!passed) {
            // to log in the end of the execution again
            exec();
        }
    }

    private boolean exec() throws MavenExecutionException {
        if (Boolean.parseBoolean(System.getProperty(PROP_SKIP_JVM_ARCH))) {
            logger.info("Skipping architecture validation");
            return false;
        }

        try {
            JvmArchValidator.validateJvmArch();
            return true;
        } catch (JvmArchViolationException e) {
            String message = e.getMessage();
            if (additionalFailureMessage != null) {
                message += "\n" + additionalFailureMessage;
            }
            if (policy == Policy.WARN) {
                logger.warn(ANSI_RED + "{}", message);
            } else {
                logger.info("To skip this validation, use '-D" + PROP_SKIP_JVM_ARCH + "=true' option.");
                throw new MavenExecutionException(message, new File("pom.xml"));
            }
            return false;
        }
    }
}
