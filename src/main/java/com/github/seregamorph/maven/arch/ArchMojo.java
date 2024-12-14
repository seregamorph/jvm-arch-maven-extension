package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven goal to validate the architecture of the current JVM vs real CPU to avoid overhead of Rosetta emulation.
 */
@Mojo(name = "arch", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class ArchMojo extends AbstractMojo {

    static final String PROP_SKIP_ARCH = "skipArch";

    @Override
    public void execute() throws MojoExecutionException {
        if (Boolean.parseBoolean(System.getProperty(PROP_SKIP_ARCH))) {
            getLog().info("Skipping architecture validation");
            return;
        }

        String arch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        if ("Mac OS X".equals(osName)) {
            MacOsSupport.checkArch(getLog(), arch);
        }
    }
}
