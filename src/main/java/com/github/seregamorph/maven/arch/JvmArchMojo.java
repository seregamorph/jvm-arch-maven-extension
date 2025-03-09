package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.concurrent.TimeUnit;

/**
 * Maven goal to validate the architecture of the current JVM vs real CPU to avoid overhead of Rosetta emulation.
 */
@Mojo(name = "jvm-arch", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class JvmArchMojo extends AbstractMojo {

    private static final String ANSI_RED = "\u001B[31m";

    private static final String PROP_SKIP_JVM_ARCH = "skipJvmArch";

    /**
     * Plugin behaviour in case of JVM arch does not match the real CPU arch.
     * Possible values: FAIL, WARN.
     */
    @Parameter(property = "jvmArchPolicy", defaultValue = "FAIL")
    private Policy policy = Policy.FAIL;

    @Override
    public void execute() {
        if (Boolean.parseBoolean(System.getProperty(PROP_SKIP_JVM_ARCH))) {
            getLog().info("Skipping architecture validation");
            return;
        }

        long startTime = System.nanoTime();
        try {
            JvmArchValidator.validateJvmArch();
        } catch (JvmArchViolationException e) {
            if (policy == Policy.WARN) {
                getLog().warn(ANSI_RED + e.getMessage());
            } else {
                getLog().info("To skip this validation, use '-D" + PROP_SKIP_JVM_ARCH + "=true' option.");
                throw e;
            }
        } finally {
            long executionTimeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            getLog().debug("Execution time: " + executionTimeMs + "ms");
        }
    }
}
