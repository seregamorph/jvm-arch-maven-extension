package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.concurrent.TimeUnit;

/**
 * Maven goal to validate the architecture of the current JVM vs real CPU to avoid overhead of Rosetta emulation.
 */
@Mojo(name = "jvm-arch", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class JvmArchMojo extends AbstractMojo {

    static final String PROP_SKIP_JVM_ARCH = "skipJvmArch";

    @Override
    public void execute() throws MojoExecutionException {
        if (Boolean.parseBoolean(System.getProperty(PROP_SKIP_JVM_ARCH))) {
            getLog().info("Skipping architecture validation");
            return;
        }

        long startTime = System.nanoTime();
        try {
            String osArch = System.getProperty("os.arch");
            String osName = System.getProperty("os.name");
            getLog().debug("os.arch: " + osArch);
            getLog().debug("os.name: " + osName);
            if ("Mac OS X".equals(osName)) {
                MacOsSupport.checkArch(getLog(), osArch);
            } else if ("Windows 11".equals(osName)) {
                Windows11Support.checkArch(getLog(), osArch);
            }
        } finally {
            long executionTimeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            getLog().debug("Execution time: " + executionTimeMs + "ms");
        }
    }
}
