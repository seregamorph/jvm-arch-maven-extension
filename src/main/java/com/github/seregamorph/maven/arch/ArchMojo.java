package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Mojo(name = "arch", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ArchMojo extends AbstractMojo {

    private static final String PROP_SKIP_ARCH = "skipArch";

    public void execute() throws MojoExecutionException {
        if (Boolean.parseBoolean(System.getProperty(PROP_SKIP_ARCH))) {
            getLog().info("Skipping architecture validation");
            return;
        }

        String arch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        if ("x86_64".equals(arch) && "Mac OS X".equals(osName)) {
            // The Maven is started on macOS intel-based JVM, but it can be Apple Silicon CPU
            // So, we need to check the real architecture
            try {
                // Special notes. Both "uname -m" and "machine" commands will not give correct result,
                // because in case of Rosetta 2, they will return "x86_64" or "i486" instead of "arm64"/"arm64e".
                getLog().info("Executing 'sysctl machdep.cpu.brand_string'");
                Process process = Runtime.getRuntime().exec("sysctl machdep.cpu.brand_string");
                if (process.waitFor(5, TimeUnit.SECONDS)) {
                    try (InputStream in = process.getInputStream()) {
                        String cpuBrand = read(in).trim();
                        getLog().info(cpuBrand);
                        // Sample values:
                        // "machdep.cpu.brand_string: Apple M1", "machdep.cpu.brand_string: Apple M3 Pro" for Apple Silicon
                        // "machdep.cpu.brand_string: Intel(R) Core(TM) i7-7700HQ CPU @ 2.80GHz" for Intel
                        if (cpuBrand.contains(" Apple")) {
                            throw new MojoExecutionException("The Maven is started on macOS intel-based JVM, " +
                                    "but the real CPU is Apple Silicon. To avoid performance overhead, please use " +
                                    "proper JVM for Apple Silicon.\n" +
                                    "To skip this validation, use '-D" + PROP_SKIP_ARCH + "=true' option.");
                        }
                    }
                } else {
                    getLog().warn("Failed to await for CPU brand");
                }
            } catch (IOException | InterruptedException e) {
                getLog().error("Failed to get real architecture", e);
            }
        }
    }

    private static String read(InputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, read));
        }
        return builder.toString();
    }
}
